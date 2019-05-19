package edu.yuferov.messagebroker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RequestHandleService {
    private static final CSVFormat FORMAT = CSVFormat.EXCEL.withDelimiter(';');
    private static final String PREFIX = "./static/";
    private static final String SUFFIX = ".csv";

    public InputStreamResource getResultFile(String fileId) {
        String filename = formatFileName(fileId);
        try {
            return new InputStreamResource(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RequestHandleServiceException("wrong file id", e);
        }
    }

    public String handleFile(int field, int count, MultipartFile file) {
        List<CSVRecord> result = parseFile(file);
        result = performOperations(result, field, count);
        return writeResultToFile(result);
    }

    private List<CSVRecord> parseFile(MultipartFile file) {
        try (
                InputStream input = file.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(input);
                CSVParser parser = new CSVParser(inputStreamReader, FORMAT)
        ) {
            return parser.getRecords();
        } catch (IOException e) {
            throw new RequestHandleServiceException("cannot handle the file", e);
        }
    }

    private List<CSVRecord> performOperations(List<CSVRecord> input, int field, int count) {
        final Comparator<CSVRecord> comparator = Comparator.comparing(lhs -> lhs.get(field));
        try {
            return input.stream()
                    .sorted(comparator)
                    .limit(count)
                    .collect(Collectors.toList());
        } catch (IndexOutOfBoundsException e) {
            throw new RequestHandleServiceException("wrong field value", e);
        }
    }

    private String writeResultToFile(List<CSVRecord> result) {
        final String fileId = UUID.randomUUID().toString();
        final String filename = formatFileName(fileId);
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filename), FORMAT)) {
            printer.printRecords(result);
            printer.flush();
        } catch (IOException e) {
            throw new RequestHandleServiceException("cannot save file", e);
        }
        return fileId;
    }

    private String formatFileName(String fileId) {
        return PREFIX + fileId + SUFFIX;
    }
}

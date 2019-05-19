package edu.yuferov.messagebroker.controller;

import edu.yuferov.messagebroker.service.RequestHandleService;
import edu.yuferov.messagebroker.service.RequestHandleServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/top")
public class RequestHandleController {
    @Autowired
    private RequestHandleService service;

    @GetMapping
    String get() {
        return "index";
    }

    @GetMapping("/file")
    ResponseEntity<Resource> getFile(@RequestParam String id) {
        InputStreamResource resultFile = service.getResultFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.csv");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.valueOf("text/csv"))
                .body(resultFile);
    }

    @PostMapping
    String post(
            @RequestParam Integer field,
            @RequestParam Integer count,
            @RequestParam MultipartFile file,
            Model model
    ) {
        String fileId = service.handleFile(field, count, file);
        model.addAttribute("filename", formatFileLink(fileId));
        return "success";
    }

    @ExceptionHandler
    String error(RequestHandleServiceException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "index";
    }

    private String formatFileLink(String fileId) {
        return "/top/file?id=" + fileId;
    }
}

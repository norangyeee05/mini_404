package com.project.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.project.service.ResourceService.DownloadFile;


final class FileHelper {

    private FileHelper() {
    }


    static List<MultipartFile> merge(List<MultipartFile> files, List<MultipartFile> bracketFiles) {
        List<MultipartFile> all = new ArrayList<>();
        if (files != null) {
            all.addAll(files);
        }
        if (bracketFiles != null) {
            all.addAll(bracketFiles);
        }
        return all;
    }

    static ResponseEntity<Resource> download(DownloadFile file) {
        MediaType type;
        try {
            type = file.contentType() == null
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(file.contentType());
        } catch (Exception e) {
            type = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.fileName(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(new FileSystemResource(file.path()));
    }
}

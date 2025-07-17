package com.maroots.backend.controller;

import com.maroots.backend.model.FileEntity;
import com.maroots.backend.repository.FileRepository;
import com.maroots.backend.security.JwtService;
import com.maroots.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final S3Service s3Service;
    private final FileRepository fileRepository;
    private final JwtService jwtService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        String s3Key = s3Service.uploadFile(file);

        FileEntity metadata = FileEntity.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size(file.getSize())
                .s3Key(s3Key)
                .ownerEmail(principal.getName())
                .build();
        return ResponseEntity.ok(fileRepository.save(metadata));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable UUID id){
        FileEntity file = fileRepository.findById(id).orElseThrow();
        byte[] data = s3Service.downloadFile(file.getS3Key());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +file.getName())
                .body(data);
    }




}

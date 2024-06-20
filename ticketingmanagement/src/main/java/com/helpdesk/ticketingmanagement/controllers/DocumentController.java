package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.entities.Document;
import com.helpdesk.ticketingmanagement.services.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/document")
@EnableMethodSecurity
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/ticket/{ticketId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> uploadDocForTicket(@RequestParam("files") MultipartFile[] files, @PathVariable Long ticketId) throws Exception
    {
        documentService.uploadDocForTicket(files, ticketId);
        return new ResponseEntity<>("File Uploaded successfully", HttpStatus.OK);
    }

    @PostMapping("/profile/{userId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> uploadDocForUser(@RequestParam("files") MultipartFile file, @PathVariable Long userId) throws Exception
    {
        documentService.uploadDocForUser(file, userId);
        return new ResponseEntity<>("File Uploaded successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public Document downloadDoc(@PathVariable Long id) throws Exception
    {
        return documentService.download(id);

//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(document.getContentType()))
//                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + document.getDocumentName()
//                        + "\"")
//                .body(new ByteArrayResource(document.getData()));
    }

}



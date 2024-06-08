package com.helpdesk.ticketingmanagement.controllers;

import com.helpdesk.ticketingmanagement.entities.Document;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.services.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @PostMapping("/comment/{commentId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> uploadDocForComment(@RequestParam("files") MultipartFile[] files, @PathVariable Long commentId) throws Exception
    {
        documentService.uploadDocForComment(files, commentId);
        return new ResponseEntity<>("File Uploaded successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Resource> downloadDoc(@PathVariable Long id) throws Exception
    {
        Document document = documentService.download(id);


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + document.getDocumentName()
                        + "\"")
                .body(new ByteArrayResource(document.getData()));
    }

}

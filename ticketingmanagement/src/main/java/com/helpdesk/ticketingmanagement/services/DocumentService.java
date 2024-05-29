package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.Document;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    void upload(MultipartFile[] files, Long ticketId) throws Exception;

    Document download(Long id) throws Exception;
}

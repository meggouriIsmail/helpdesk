package com.helpdesk.ticketingmanagement.services;

import com.helpdesk.ticketingmanagement.entities.Document;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    void uploadDocForTicket(MultipartFile[] files, Long ticketId) throws Exception;
    void uploadDocForUser(MultipartFile file, Long userId) throws Exception;

    Document download(Long id) throws Exception;
}

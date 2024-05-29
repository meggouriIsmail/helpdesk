package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.entities.Document;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.repositories.DocumentRepository;
import com.helpdesk.ticketingmanagement.repositories.TicketRepository;
import com.helpdesk.ticketingmanagement.services.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final TicketRepository ticketRepository;
    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(TicketRepository ticketRepository, DocumentRepository documentRepository) {
        this.ticketRepository = ticketRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public void upload(MultipartFile[] files, Long ticketId) throws Exception {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);

        Set<Document> documents = new HashSet<>();

        try {
            for (MultipartFile file : files) {
                Document document = new Document();
                document.setDocumentName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
                document.setContentType(file.getContentType());
                document.setData(file.getBytes());
                document.setSize(file.getSize());
                document.setTicket(ticket.orElseThrow());

                documents.add(document);
            }

            documentRepository.saveAll(documents);

        } catch (Exception e) {
            throw new Exception("Could not save files :" + e.getMessage());
        }
    }

    @Override
    public Document download(Long id) throws Exception {
        return documentRepository.findById(id).orElseThrow(() -> new Exception("File not found with Id " + id));
    }
}

package com.helpdesk.ticketingmanagement.services.Impl;

import com.helpdesk.ticketingmanagement.entities.Comment;
import com.helpdesk.ticketingmanagement.entities.Document;
import com.helpdesk.ticketingmanagement.entities.Ticket;
import com.helpdesk.ticketingmanagement.repositories.CommentRepository;
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
    private final CommentRepository commentRepository;

    public DocumentServiceImpl(TicketRepository ticketRepository, DocumentRepository documentRepository, CommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.documentRepository = documentRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void uploadDocForTicket(MultipartFile[] files, Long ticketId) throws Exception {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            upload(files, ticketOptional.get());
        }
    }

    @Override
    public void uploadDocForComment(MultipartFile[] files, Long commentId) throws Exception {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            upload(files, commentOptional.get());
        }
    }

    public void upload(MultipartFile[] files, Object entity) throws Exception {

        Set<Document> documents = new HashSet<>();

        try {
            for (MultipartFile file : files) {
                Document document = new Document();
                document.setDocumentName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
                document.setContentType(file.getContentType());
                document.setData(file.getBytes());
                document.setSize(file.getSize());

                if (entity instanceof Ticket ticket) {
                    document.setTicket(ticket);
                } else if (entity instanceof Comment comment) {
                    document.setComment(comment);
                }

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

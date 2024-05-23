package com.helpdesk.ticketingmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String title;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private TicketType type;
    private String status;
    private String priority;
    private String impact;
    private String user;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ticket")
    private List<Comment> comments;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ticket", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Set<Document> documents;
}

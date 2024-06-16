package com.helpdesk.ticketingmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ticket implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String description;
    private String title;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private TicketType type;
    private String status;
    private String priority;
    private String isResolved;
    private boolean isFavorite;
    @CreationTimestamp
    private Date createdTime;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "assignedTo")
    private User assignedTo;

    @OneToMany
    @JoinColumn(name = "sharedWith")
    private List<User> sharedWith;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ticket", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    private Set<Document> documents;
}
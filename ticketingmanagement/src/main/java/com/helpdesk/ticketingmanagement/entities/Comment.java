package com.helpdesk.ticketingmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {
    @Id
    private Long id;
    private String file;
    private String text;
    @ManyToOne
    @JoinColumn(referencedColumnName = "ticket_id")
    @JsonBackReference
    private Ticket ticket;
}

package com.helpdesk.ticketingmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.helpdesk.ticketingmanagement.enums.TypeActivity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonBackReference
    private Ticket ticket;
    @ManyToOne
    private User author;
    private Date time;
    private TypeActivity typeActivity;
    private String comment;
    private String status;
    @ManyToOne
    private User assignedTo;
    @OneToMany
    private List<User> shared_with;

}

package com.helpdesk.ticketingmanagement.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "document")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String documentName;
	
	private String contentType;
	
	private Long size;
	
	@CreationTimestamp
	private Date creationDate;

	@Lob
	@Column(name = "data", columnDefinition = "LONGBLOB")
	private byte[] data;
	
	@ManyToOne
	@JoinColumn(name = "ticket_id")
	private Ticket ticket;
	@ManyToOne
	@JoinColumn(name = "comment_id")
	private Comment comment;
	
//	@ManyToOne()
//	@JoinColumn(name = "doctype_id", nullable = false)
//	private DocType type;
}

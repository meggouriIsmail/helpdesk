package com.helpdesk.ticketingmanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "doctype")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor @Builder
public class DocType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Size(min = 3, max = 20) @Column(nullable = false)
	private String name;
	
	@CreationTimestamp
	private Date timestamp;

}

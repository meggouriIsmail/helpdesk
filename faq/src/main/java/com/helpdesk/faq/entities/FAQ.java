package com.helpdesk.faq.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "FAQ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQ {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String content;

	private String description;

	@CreationTimestamp
	private Date postingDate;

	private int votes;
	
	private int votesUp;
	
	private int votesDown;

	@Column(nullable = false)
	private Boolean status;

}

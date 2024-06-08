package com.helpdesk.faq.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

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

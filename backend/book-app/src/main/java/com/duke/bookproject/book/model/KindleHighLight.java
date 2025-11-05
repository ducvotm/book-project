package com.duke.bookproject.book.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KindleHighLight {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@NotBlank
	private String userEmail;

	@NotNull
	@NotBlank
	private String title;

	@NotNull
	@NotBlank
	private String author;

	private String content;

}

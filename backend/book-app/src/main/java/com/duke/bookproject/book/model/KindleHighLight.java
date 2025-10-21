package com.duke.bookproject.book.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.book.model.Book;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KindleHighLight {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	private String content;

}

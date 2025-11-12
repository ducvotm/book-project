package com.duke.bookproject.book.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HighlightReminder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@NotBlank
	private String userEmail;

	@NotNull
	private Long highlightId;

	@NotNull
	private LocalDate nextReminderDate;

	private boolean enabled;

	public static HighlightReminder dailyReminder(String userEmail, Long highlightId) {
		LocalDate tomorrow = LocalDate.now().plusDays(1);

		return HighlightReminder.builder()
				.userEmail(userEmail)
				.highlightId(highlightId)
				.nextReminderDate(tomorrow)
				.enabled(true)
				.build();
	}

}

package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.repository.HighlightReminderRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HighlightReminderService {

	private final HighlightReminderRepository repository;

	public HighlightReminderService(HighlightReminderRepository repository) {
		this.repository = repository;
	}

	public void createReminder(@NonNull String userEmail, @NonNull Long highlightId) {
		HighlightReminder reminder = HighlightReminder.dailyReminder(userEmail, highlightId);
		repository.save(reminder);
	}

	public List<HighlightReminder> findRemindersDueToday() {
		return repository.findByNextReminderDateAndEnabled(LocalDate.now(), true);
	}

}

package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.repository.HighlightReminderRepository;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

@Service

	

	
	
	

	blic HighlightReminderService(HighlightReminderRepository repository) {
		this.repository = repository;
	}

	public void createReminder(@NonNull String userEmail, @NonNull Long highlightId) {
		High

	ightReminder reminder = HighlightReminder.dailyReminder(userEmail, highlightId);
		repository.save(reminder);
	}

	public List<HighlightRemider> findRemindersDueToday() {
		return repository.findByNextReminderDateAndEnabled(LocalDate.now(), true);
	}

}

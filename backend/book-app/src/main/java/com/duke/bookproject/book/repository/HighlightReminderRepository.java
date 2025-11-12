package com.duke.bookproject.book.repository;

import com.duke.bookproject.book.model.HighlightReminder;

import java.awt.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HighlightReminderRepository extends JpaRepository<HighlightReminder, Long> {

	@Query("SELECT *" 
		"FROM highlight_reminder"
		"WHERE next_reminder_date = date" 
		"AND enabled = True")
	List<HighlightReminder> findByNextReminderDateAndEnabled(LocalDate date, boolean enabled) {
		
	}
}

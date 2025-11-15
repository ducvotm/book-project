package com.duke.bookproject.book.repository;

import com.duke.bookproject.book.model.HighlightReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HighlightReminderRepository extends JpaRepository<HighlightReminder, Long> {

	@Query("SELECT r FROM HighlightReminder r WHERE r.nextReminderDate = :date AND r.enabled = :enabled")
	List<HighlightReminder> findByNextReminderDateAndEnabled(@Param("date") LocalDate date,
			@Param("enabled") boolean enabled);
}

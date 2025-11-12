package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.repository.HighlightReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HighlightReminderServiceTest {

	@Mock
	private HighlightReminderRepository repository;

	private HighlightReminderService service;

	@BeforeEach
	void setUp() {
		service = new HighlightReminderService(repository);
	}

	@Test
	void shouldCreateReminder_whenUserEmailAndHighlightIdAreProvided() {
		String userEmail = "user@example.com";
		Long highlightId = 42L;
		LocalDate today = LocalDate.now();

		service.createReminder(userEmail, highlightId);

		ArgumentCaptor<HighlightReminder> captor = ArgumentCaptor.forClass(HighlightReminder.class);
		verify(repository).save(captor.capture());

		HighlightReminder saved = captor.getValue();
		assertThat(saved.getUserEmail()).isEqualTo(userEmail);
		assertThat(saved.getHighlightId()).isEqualTo(highlightId);
		assertThat(saved.getNextReminderDate()).isEqualTo(today.plusDays(1));
		assertThat(saved.isEnabled()).isTrue();
	}
}

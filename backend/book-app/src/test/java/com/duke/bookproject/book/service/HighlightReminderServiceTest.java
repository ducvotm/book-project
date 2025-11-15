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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

	@Test
	void shouldFindRemindersDueToday_whenCalled() {
		LocalDate today = LocalDate.now();
		HighlightReminder reminder1 = HighlightReminder.builder()
				.id(1L)
				.userEmail("user1@example.com")
				.highlightId(10L)
				.nextReminderDate(today)
				.enabled(true)
				.build();

		HighlightReminder reminder2 = HighlightReminder.builder()
				.id(2L)
				.userEmail("user2@example.com")
				.highlightId(20L)
				.nextReminderDate(today)
				.enabled(true)
				.build();

		List<HighlightReminder> expectedReminders = Arrays.asList(reminder1, reminder2);

		when(repository.findByNextReminderDateAndEnabled(any(LocalDate.class), eq(true)))
				.thenReturn(expectedReminders);

		List<HighlightReminder> result = service.findRemindersDueToday();

		verify(repository).findByNextReminderDateAndEnabled(today, true);
		assertThat(result).hasSize(2);
		assertThat(result).containsExactlyInAnyOrderElementsOf(expectedReminders);
	}

	@Test
	void shouldUpdateNextReminderDateToTomorrow_whenReminderIsUpdated() {
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);

		HighlightReminder reminder = HighlightReminder.builder()
				.id(1L)
				.userEmail("user@example.com")
				.highlightId(42L)
				.nextReminderDate(today)
				.enabled(true)
				.build();

		service.updateNextReminderDate(reminder);

		ArgumentCaptor<HighlightReminder> captor = ArgumentCaptor.forClass(HighlightReminder.class);
		verify(repository).save(captor.capture());

		HighlightReminder updated = captor.getValue();
		assertThat(updated.getNextReminderDate()).isEqualTo(tomorrow);
		assertThat(updated.getId()).isEqualTo(1L);
		assertThat(updated.getUserEmail()).isEqualTo("user@example.com");
		assertThat(updated.getHighlightId()).isEqualTo(42L);
		assertThat(updated.isEnabled()).isTrue();
	}
}

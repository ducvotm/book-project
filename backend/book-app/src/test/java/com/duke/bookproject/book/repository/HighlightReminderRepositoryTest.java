package com.duke.bookproject.book.repository;

import com.duke.bookproject.annotations.DataJpaIntegrationTest;
import com.duke.bookproject.book.model.HighlightReminder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaIntegrationTest
class HighlightReminderRepositoryTest {

   @Autowired
   private HighlightReminderRepository underTest;

   @BeforeEach
   void setUp() {
      underTest.deleteAll();
   }

   @Test
   void shouldPersistReminderWithDailyDefaults() {
      LocalDate today = LocalDate.now();
      HighlightReminder reminder = HighlightReminder.dailyReminder("reader@example.com", 42L);

      HighlightReminder saved = underTest.save(reminder);

      Optional<HighlightReminder> found = underTest.findById(saved.getId());

      assertThat(found).isPresent();
      assertThat(found.get().getUserEmail()).isEqualTo("reader@example.com");
      assertThat(found.get().getHighlightId()).isEqualTo(42L);
      assertThat(found.get().getNextReminderDate()).isEqualTo(today.plusDays(1));
      assertThat(found.get().isEnabled()).isTrue();
   }

   @Test
   void shouldFindRemindersDueToday_whenEnabled() {
      LocalDate today = LocalDate.now();
      LocalDate yesterday = today.minusDays(1);
      LocalDate tomorrow = today.plusDays(1);

      HighlightReminder reminderDueToday1 = HighlightReminder.builder()
            .userEmail("user1@example.com")
            .highlightId(1L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      HighlightReminder reminderDueToday2 = HighlightReminder.builder()
            .userEmail("user2@example.com")
            .highlightId(2L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      HighlightReminder reminderDueYesterday = HighlightReminder.builder()
            .userEmail("user3@example.com")
            .highlightId(3L)
            .nextReminderDate(yesterday)
            .enabled(true)
            .build();

      HighlightReminder reminderDueTomorrow = HighlightReminder.builder()
            .userEmail("user4@example.com")
            .highlightId(4L)
            .nextReminderDate(tomorrow)
            .enabled(true)
            .build();

      HighlightReminder disabledReminderDueToday = HighlightReminder.builder()
            .userEmail("user5@example.com")
            .highlightId(5L)
            .nextReminderDate(today)
            .enabled(false)
            .build();

      underTest.save(reminderDueToday1);
      underTest.save(reminderDueToday2);
      underTest.save(reminderDueYesterday);
      underTest.save(reminderDueTomorrow);
      underTest.save(disabledReminderDueToday);

      List<HighlightReminder> remindersDueToday = underTest.findByNextReminderDateAndEnabled(today, true);

      assertThat(remindersDueToday).hasSize(2);
      assertThat(remindersDueToday).extracting(HighlightReminder::getHighlightId)
            .containsExactlyInAnyOrder(1L, 2L);
      assertThat(remindersDueToday).allMatch(HighlightReminder::isEnabled);
      assertThat(remindersDueToday).allMatch(r -> r.getNextReminderDate().equals(today));
   }
}

package com.duke.bookproject.book.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class HighlightReminderTest {

   @Test
   void shouldCreateDailyReminderWithDefaultValues() {
      String userEmail = "reader@example.com";
      Long highlightId = 42L;
      LocalDate today = LocalDate.now();

      HighlightReminder reminder = HighlightReminder.dailyReminder(userEmail, highlightId);

      assertThat(reminder.getUserEmail()).isEqualTo(userEmail);
      assertThat(reminder.getHighlightId()).isEqualTo(highlightId);
      assertThat(reminder.getNextReminderDate()).isEqualTo(today.plusDays(1));
      assertThat(reminder.isEnabled()).isTrue();
   }
}

package com.duke.bookproject.book.scheduler;

import com.duke.bookproject.book.service.DailyReminderScheduler;
import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.service.EmailService;
import com.duke.bookproject.book.service.HighlightReminderService;
import com.duke.bookproject.book.service.KindleHighLightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyReminderSchedulerTest {

   @Mock
   private HighlightReminderService reminderService;

   @Mock
   private KindleHighLightService highlightService;

   @Mock
   private EmailService emailService;

   private DailyReminderScheduler scheduler;

   @BeforeEach
   void setUp() {
      scheduler = new DailyReminderScheduler(reminderService, highlightService, emailService);
   }

   @Test
   void shouldSendEmailAndUpdateReminders_whenRemindersDueTodayExist() throws MessagingException {
      LocalDate today = LocalDate.now();

      String userEmail1 = "user1@example.com";
      String userEmail2 = "user2@example.com";

      HighlightReminder reminder1 = HighlightReminder.builder()
            .id(1L)
            .userEmail(userEmail1)
            .highlightId(10L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      HighlightReminder reminder2 = HighlightReminder.builder()
            .id(2L)
            .userEmail(userEmail1)
            .highlightId(20L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      HighlightReminder reminder3 = HighlightReminder.builder()
            .id(3L)
            .userEmail(userEmail2)
            .highlightId(30L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      List<HighlightReminder> reminders = Arrays.asList(reminder1, reminder2, reminder3);

      KindleHighLight highlight1 = KindleHighLight.builder()
            .id(10L)
            .userEmail(userEmail1)
            .title("Book 1")
            .author("Author 1")
            .content("Highlight 1 content")
            .build();

      KindleHighLight highlight2 = KindleHighLight.builder()
            .id(20L)
            .userEmail(userEmail1)
            .title("Book 2")
            .author("Author 2")
            .content("Highlight 2 content")
            .build();

      KindleHighLight highlight3 = KindleHighLight.builder()
            .id(30L)
            .userEmail(userEmail2)
            .title("Book 3")
            .author("Author 3")
            .content("Highlight 3 content")
            .build();

      when(reminderService.findRemindersDueToday()).thenReturn(reminders);
      when(highlightService.findById(10L)).thenReturn(Optional.of(highlight1));
      when(highlightService.findById(20L)).thenReturn(Optional.of(highlight2));
      when(highlightService.findById(30L)).thenReturn(Optional.of(highlight3));
      when(emailService.getUsernameFromEmail(userEmail1)).thenReturn("user1");
      when(emailService.getUsernameFromEmail(userEmail2)).thenReturn("user2");

      scheduler.sendDailyReminders();

      verify(reminderService).findRemindersDueToday();
      verify(highlightService, times(3)).findById(any());
      verify(emailService, times(2)).sendMessageUsingThymeleafTemplate(
            anyString(), anyString(), anyMap());
      verify(reminderService, times(3)).updateNextReminderDate(any(HighlightReminder.class));
   }

   @Test
   void shouldNotSendEmail_whenNoRemindersDueToday() throws MessagingException {
      when(reminderService.findRemindersDueToday()).thenReturn(List.of());

      scheduler.sendDailyReminders();

      verify(reminderService).findRemindersDueToday();
      verify(highlightService, never()).findById(any());
      verify(emailService, never()).sendMessageUsingThymeleafTemplate(
            anyString(), anyString(), anyMap());
      verify(reminderService, never()).updateNextReminderDate(any(HighlightReminder.class));
   }

   @Test
   void shouldGroupHighlightsByUser_whenMultipleUsersHaveReminders() throws MessagingException {
      LocalDate today = LocalDate.now();
      String userEmail = "user@example.com";

      HighlightReminder reminder1 = HighlightReminder.builder()
            .id(1L)
            .userEmail(userEmail)
            .highlightId(10L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      HighlightReminder reminder2 = HighlightReminder.builder()
            .id(2L)
            .userEmail(userEmail)
            .highlightId(20L)
            .nextReminderDate(today)
            .enabled(true)
            .build();

      List<HighlightReminder> reminders = Arrays.asList(reminder1, reminder2);

      KindleHighLight highlight1 = KindleHighLight.builder()
            .id(10L)
            .userEmail(userEmail)
            .title("Book 1")
            .author("Author 1")
            .content("Highlight 1")
            .build();

      KindleHighLight highlight2 = KindleHighLight.builder()
            .id(20L)
            .userEmail(userEmail)
            .title("Book 2")
            .author("Author 2")
            .content("Highlight 2")
            .build();

      when(reminderService.findRemindersDueToday()).thenReturn(reminders);
      when(highlightService.findById(10L)).thenReturn(Optional.of(highlight1));
      when(highlightService.findById(20L)).thenReturn(Optional.of(highlight2));
      when(emailService.getUsernameFromEmail(userEmail)).thenReturn("user");

      @SuppressWarnings("unchecked")
      ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
      @SuppressWarnings("unchecked")
      ArgumentCaptor<Map<String, Object>> templateCaptor = ArgumentCaptor.forClass(Map.class);

      scheduler.sendDailyReminders();

      verify(emailService).sendMessageUsingThymeleafTemplate(
            emailCaptor.capture(), anyString(), templateCaptor.capture());

      assertThat(emailCaptor.getValue()).isEqualTo(userEmail);
      Map<String, Object> template = templateCaptor.getValue();
      assertThat(template).isNotNull();
      verify(reminderService, times(2)).updateNextReminderDate(any(HighlightReminder.class));
   }
}

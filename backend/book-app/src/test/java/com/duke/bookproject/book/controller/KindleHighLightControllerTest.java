package com.duke.bookproject.book.controller;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.account.service.UserService;
import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.service.EmailService;
import com.duke.bookproject.book.service.HighlightReminderService;
import com.duke.bookproject.book.service.KindleHighLightService;
import com.duke.bookproject.book.service.KindleHiglightsParser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

class KindleHighLightControllerTest {

   private final KindleHighLightController controller;
   private final KindleHighLightService mockedService;
   private final KindleHiglightsParser mockedParser;
   private final EmailService mockedEmailService;
   private final UserService mockedUserService;
   private final HighlightReminderService mockedReminderService;

   KindleHighLightControllerTest() {
      mockedService = mock(KindleHighLightService.class);
      mockedParser = mock(KindleHiglightsParser.class);
      mockedEmailService = mock(EmailService.class);
      mockedUserService = mock(UserService.class);
      mockedReminderService = mock(HighlightReminderService.class);
      controller = new KindleHighLightController(mockedService, mockedParser, mockedEmailService, mockedUserService, mockedReminderService);
   }

   @Test
   void shouldReturnAllHighlights() {
      List<KindleHighLight> highlights = new ArrayList<>();
      highlights.add(KindleHighLight.builder()
            .title("Test Book")
            .author("Test Author")
            .content("Test content")
            .build());

      when(mockedService.findAll()).thenReturn(highlights);

      List<KindleHighLight> result = controller.findAll();

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getTitle()).isEqualTo("Test Book");
   }

   @Test
   void shouldReturnEmptyList_whenNoHighlightsExist() {
      when(mockedService.findAll()).thenReturn(new ArrayList<>());

      assertThat(controller.findAll()).isEmpty();
   }

   @Test
   void findById_returnsHighlight_ifPresent() {
      KindleHighLight highlight = KindleHighLight.builder()
            .title("Test Book")
            .author("Test Author")
            .content("Test content")
            .build();
      when(mockedService.findById(any(Long.class))).thenReturn(Optional.of(highlight));

      assertThat(controller.findById(1L)).isEqualTo(highlight);
   }

   @Test
   void findById_returnsNotFound_ifHighlightIsEmpty() {
      when(mockedService.findById(any(Long.class))).thenReturn(Optional.empty());

      assertThatExceptionOfType(ResponseStatusException.class)
            .isThrownBy(() -> controller.findById(1L));
   }

   @Test
   void delete_returnsNotFound_ifHighlightDoesNotExist() {
      when(mockedService.findById(any(Long.class))).thenReturn(Optional.empty());

      ResponseEntity<String> response = controller.delete(1L);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
   }

   @Test
   void delete_returnsOk_ifHighlightExists() {
      when(mockedService.findById(any(Long.class)))
            .thenReturn(Optional.of(KindleHighLight.builder().build()));

      ResponseEntity<String> response = controller.delete(1L);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
   }

   @Test
   void shouldImportHighlightsFromFile() {
      String fileContent = "nexus_yuval noah harari (Yuval Noah Harari)\n" +
            "- Your Highlight on page xiii | Location 103-104\n\n" +
            "Power stems from cooperation";

      List<KindleHighLight> parsedHighlights = new ArrayList<>();
      parsedHighlights.add(KindleHighLight.builder()
            .title("nexus_yuval noah harari")
            .author("Yuval Noah Harari")
            .content("Power stems from cooperation")
            .build());

      when(mockedParser.parseMultipleHighlights(anyString())).thenReturn(parsedHighlights);

      ResponseEntity<String> response = controller.importHighlights(fileContent);

      verify(mockedParser).parseMultipleHighlights(fileContent);
      verify(mockedService).saveAll(anyList());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).contains("1");
   }

   @Test
   void shouldCreateReminders_whenHighlightsAreImported() {
      String fileContent = "nexus_yuval noah harari (Yuval Noah Harari)\n" +
            "- Your Highlight on page xiii | Location 103-104\n\n" +
            "Power stems from cooperation";

      String userEmail = "user@example.com";
      User mockUser = User.builder().email(userEmail).build();

      List<KindleHighLight> parsedHighlights = new ArrayList<>();
      KindleHighLight highlight1 = KindleHighLight.builder()
            .id(1L)
            .userEmail(userEmail)
            .title("nexus_yuval noah harari")
            .author("Yuval Noah Harari")
            .content("Power stems from cooperation")
            .build();
      parsedHighlights.add(highlight1);

      List<KindleHighLight> savedHighlights = new ArrayList<>();
      savedHighlights.add(highlight1);

      when(mockedParser.parseMultipleHighlights(anyString())).thenReturn(parsedHighlights);
      when(mockedService.saveAll(anyList())).thenReturn(savedHighlights);
      when(mockedUserService.getCurrentUser()).thenReturn(mockUser);

      ResponseEntity<String> response = controller.importHighlights(fileContent);

      verify(mockedParser).parseMultipleHighlights(fileContent);
      verify(mockedService).saveAll(parsedHighlights);
      verify(mockedReminderService).createReminder(userEmail, 1L);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).contains("1");
   }

   @Test
   void emailHighlights_sendsEmail_whenHighlightsExist() throws MessagingException {
      List<KindleHighLight> highlights = new ArrayList<>();
      highlights.add(KindleHighLight.builder()
            .title("Nexus")
            .author("Yuval Noah Harari")
            .content("Power always stems from cooperation")
            .build());
      highlights.add(KindleHighLight.builder()
            .title("Coders at Work")
            .author("Peter Seibel")
            .content("The design process is definitely an ongoing thing")
            .build());

      User mockUser = User.builder().email("user@example.com").build();
      when(mockedService.findAll()).thenReturn(highlights);
      when(mockedUserService.getCurrentUser()).thenReturn(mockUser);

      ResponseEntity<String> response = controller.emailHighlights();

      verify(mockedEmailService, times(1)).sendMessageUsingThymeleafTemplate(
            eq("user@example.com"),
            anyString(),
            anyMap());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("Email sent successfully");
   }

   @Test
   void emailHighlights_returnsMessage_whenNoHighlights() throws MessagingException {
      when(mockedService.findAll()).thenReturn(new ArrayList<>());

      ResponseEntity<String> response = controller.emailHighlights();

      verify(mockedEmailService, times(0)).sendMessageUsingThymeleafTemplate(anyString(), anyString(), anyMap());
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isEqualTo("No highlights to email");
   }
}

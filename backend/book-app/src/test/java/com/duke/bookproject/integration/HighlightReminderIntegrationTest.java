package com.duke.bookproject.integration;

import com.duke.bookproject.BookProjectApplication;
import com.duke.bookproject.account.dto.UserToRegisterDto;
import com.duke.bookproject.book.model.HighlightReminder;
import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.repository.HighlightReminderRepository;
import com.duke.bookproject.book.repository.KindleHighLightRepository;
import com.duke.bookproject.book.service.DailyReminderScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.mail.MessagingException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BookProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("Integration")
class HighlightReminderIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private DailyReminderScheduler scheduler;

   @Autowired
   private HighlightReminderRepository reminderRepository;

   @Autowired
   private KindleHighLightRepository highlightRepository;

   private final String AUTHORIZATION = "Authorization";
   private final String url = "http://localhost:8080";
   private String jwtToken = "";
   private String userEmail = "user@user.user";

   @BeforeEach
   void loginAndGetToken() throws Exception {
      reminderRepository.deleteAll();
      highlightRepository.deleteAll();

      UserToRegisterDto userToRegisterDto = new UserToRegisterDto(userEmail, "password");

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
      ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
      String requestJson = writer.writeValueAsString(userToRegisterDto);

      MvcResult result = this.mockMvc
            .perform(post(url + "/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

      jwtToken = result.getResponse().getHeader(AUTHORIZATION);
   }

   @Test
   void shouldSendReminderEmail_whenUserImportsHighlightsAndSchedulerRuns() throws Exception {
      ClassPathResource resource = new ClassPathResource("kindleHighlightsSample.txt");
      String highlightContent = Files.readString(resource.getFile().toPath());

      mockMvc.perform(post(url + "/api/kindle-highlights/import")
            .header(AUTHORIZATION, jwtToken)
            .contentType(MediaType.TEXT_PLAIN)
            .content(highlightContent))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Imported 6 highlights"))
            .andReturn();

      List<KindleHighLight> savedHighlights = highlightRepository.findAll();
      assertThat(savedHighlights).hasSize(6);
      assertThat(savedHighlights).allMatch(h -> userEmail.equals(h.getUserEmail()));

      List<HighlightReminder> reminders = reminderRepository.findAll();
      assertThat(reminders).hasSize(6);
      assertThat(reminders).allMatch(r -> userEmail.equals(r.getUserEmail()));
      assertThat(reminders).allMatch(r -> r.getNextReminderDate().equals(LocalDate.now().plusDays(1)));

      for (HighlightReminder reminder : reminders) {
         reminder.setNextReminderDate(LocalDate.now());
         reminderRepository.save(reminder);
      }

      scheduler.sendDailyReminders();

      List<HighlightReminder> updatedReminders = reminderRepository.findAll();
      assertThat(updatedReminders).hasSize(6);
      assertThat(updatedReminders).allMatch(r -> r.getNextReminderDate().equals(LocalDate.now().plusDays(1)));

      mockMvc.perform(get(url + "/api/kindle-highlights")
            .header(AUTHORIZATION, jwtToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(6));
   }
}

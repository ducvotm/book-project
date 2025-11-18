package com.duke.bookproject.integration;

import com.duke.bookproject.BookProjectApplication;
import com.duke.bookproject.account.dto.UserToRegisterDto;
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

import java.nio.file.Files;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BookProjectApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("Integration")
class KindleHighLightUserIsolationIntegrationTest {

   @Autowired
   private MockMvc mockMvc;

   private final String AUTHORIZATION = "Authorization";
   private final String url = "http://localhost:8080";
   private String jwtToken = "";
   private String userEmail = "user@user.user";

   @BeforeEach
   void setUp() throws Exception {
      jwtToken = loginAndGetToken(userEmail, "password");
   }

   private String loginAndGetToken(String email, String password) throws Exception {
      UserToRegisterDto userToRegisterDto = new UserToRegisterDto(email, password);

      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
      ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
      String requestJson = writer.writeValueAsString(userToRegisterDto);

      MvcResult result = mockMvc.perform(post(url + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk())
            .andReturn();

      return result.getResponse().getHeader(AUTHORIZATION);
   }

   @Test
   void shouldOnlyReturnHighlightsForCurrentUser() throws Exception {
      ClassPathResource resource = new ClassPathResource("kindleHighlightsSample.txt");
      String highlightContent = Files.readString(resource.getFile().toPath());

      mockMvc.perform(post(url + "/api/kindle-highlights/import")
            .header(AUTHORIZATION, jwtToken)
            .contentType(MediaType.TEXT_PLAIN)
            .content(highlightContent))
            .andExpect(status().isOk());

      mockMvc.perform(get(url + "/api/kindle-highlights")
            .header(AUTHORIZATION, jwtToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(6))
            .andExpect(jsonPath("$[0].userEmail").value(userEmail))
            .andExpect(jsonPath("$[1].userEmail").value(userEmail))
            .andExpect(jsonPath("$[2].userEmail").value(userEmail))
            .andExpect(jsonPath("$[3].userEmail").value(userEmail))
            .andExpect(jsonPath("$[4].userEmail").value(userEmail))
            .andExpect(jsonPath("$[5].userEmail").value(userEmail));
   }
}

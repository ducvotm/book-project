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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = BookProjectApplication.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "../target/snippets")
@ActiveProfiles("test")
@Tag("Integration")
class KindleHighLightControllerIntegrationTest {
   @Autowired
   private MockMvc mockMvc;

   private final String AUTHORIZATION = "Authorization";
   private final String url = "http://localhost:8080";
   private String jwtToken = "";

   @BeforeEach
   void loginAndGetToken() throws Exception {
      UserToRegisterDto userToRegisterDto = new UserToRegisterDto("user@user.user", "password");

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
   void shouldImportHighlightsFromFile() throws Exception {
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

      mockMvc.perform(get(url + "/api/kindle-highlights")
            .header(AUTHORIZATION, jwtToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(6))
            .andExpect(jsonPath("$[0].title").value("nexus_yuval noah harari"))
            .andExpect(jsonPath("$[0].author").value("Yuval Noah Harari"))
            .andExpect(jsonPath("$[0].content")
                  .value("Power always stems from cooperation between large numbers of humans."))
            .andExpect(jsonPath("$[2].title").value("coders at work reflections on the craft of program"))
            .andExpect(jsonPath("$[2].author").value("Peter Seibel"))
            .andExpect(jsonPath("$[5].content")
                  .value(
                        "The design process is definitely an ongoing thing; you never know what the design is until the program is done."));
   }
}

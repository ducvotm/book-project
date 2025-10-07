package com.duke.bookproject.book.controller;

import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.service.KindleHighLightService;
import com.duke.bookproject.book.service.KindleHiglightsParser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KindleHighLightControllerTest {

   private final KindleHighLightController controller;
   private final KindleHighLightService mockedService;
   private final KindleHiglightsParser mockedParser;

   KindleHighLightControllerTest() {
      mockedService = mock(KindleHighLightService.class);
      mockedParser = mock(KindleHiglightsParser.class);
      controller = new KindleHighLightController(mockedService, mockedParser);
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
}

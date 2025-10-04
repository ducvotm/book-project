package com.duke.book.book.service;

import com.duke.book.book.model.KindleHighLight;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KindleHighlightsParserTest {

   private final KindleHiglightsParser parser = new KindleHiglightsParser();

   @Test
   void shouldParseBookTitle() {
      // GIVEN
      String line = "nexus_yuval noah harari (Yuval Noah Harari)";

      // WHEN
      String result = parser.parseTitle(line);

      // THEN
      assertThat(result).isEqualTo("nexus_yuval noah harari");
   }

   @Test
   void shouldParseAuthor() {
      // GIVEN
      String line = "nexus_yuval noah harari (Yuval Noah Harari)";

      // WHEN
      String result = parser.parseAuthor(line);

      // THEN
      assertThat(result).isEqualTo("Yuval Noah Harari");
   }

   @Test
   void shouldParseSingleHighlight() {
      // GIVEN - one complete highlight block
      String highlightBlock = "nexus_yuval noah harari (Yuval Noah Harari)\n" +
            "- Your Highlight on page xiii | Location 103-104 | Added on Monday, September 15, 2025 2:43:26 PM\n" +
            "\n" +
            "Power always stems from cooperation between large numbers of humans.";

      // WHEN
      KindleHighLight result = parser.parseSingleHighlight(highlightBlock);

      // THEN
      assertThat(result.getTitle()).isEqualTo("nexus_yuval noah harari");
      assertThat(result.getAuthor()).isEqualTo("Yuval Noah Harari");
      assertThat(result.getContent()).isEqualTo("Power always stems from cooperation between large numbers of humans.");
   }
}

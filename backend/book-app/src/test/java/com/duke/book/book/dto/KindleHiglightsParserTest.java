package com.duke.book.book.dto;

import com.duke.book.book.model.KindleHighLight;
import com.duke.book.book.service.KindleHiglightsParser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KindleHiglightsParserTest {

    private final KindleHiglightsParser parser = new KindleHiglightsParser();

    @Test
    void shouldParseBookTitle() {
        // GIVEN
        String line = "Atomic Habits (James Clear)";

        // WHEN
        KindleHighLight result = parser.parseTitleAndAuthor(line);

        // THEN
        assertThat(result.getTitle()).isEqualTo("Atomic Habits");
    }

    @Test
    void shouldParseAuthor() {
        // GIVEN
        String line = "Atomic Habits (James Clear)";

        // WHEN
        KindleHighLight result = parser.parseTitleAndAuthor(line);

        // THEN
        assertThat(result.getAuthor()).isEqualTo("James Clear");
    }
}

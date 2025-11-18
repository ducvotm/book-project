/*
* The book project lets a user keep track of different books they would like to read, are currently
* reading, have read or did not finish.
* Copyright (C) 2020  Karan Kumar

* This program is free software: you can redistribute it and/or modify it under the terms of the
* GNU General Public License as published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
* PURPOSE.  See the GNU General Public License for more details.

* You should have received a copy of the GNU General Public License along with this program.
* If not, see <https://www.gnu.org/licenses/>.
*/

package com.duke.bookproject.book.dto;

import com.duke.bookproject.book.model.KindleHighLight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("KindleHighlights should")
class KindleHighlightImportTest {

    // Helper method - creates test data dynamically
    private KindleHighLight createTestKindleHighlight() {
        return KindleHighLight.builder()
                .title("nexus_yuval noah harari")
                .author("Yuval Noah Harari")
                .userEmail("test@example.com")
                .content("Power always stems from cooperation between large numbers of humans.")
                .build();
    }

    @Test
    void haveNonNullTitleWhenBuildingWithValidData() {
        // given - a test Kindle highlight
        KindleHighLight highlight = createTestKindleHighlight();

        // then - verify title is not null and has expected value
        assertThat(highlight.getTitle()).isNotNull();
        assertThat(highlight.getTitle()).isEqualTo("nexus_yuval noah harari");
    }

    @Test
    void haveNonNullAuthorWhenBuildingWithValidData() {
        // given - a test Kindle highlight
        KindleHighLight highlight = createTestKindleHighlight();

        // then - verify author is not null and has expected value
        assertThat(highlight.getAuthor()).isNotNull();
        assertThat(highlight.getAuthor()).isEqualTo("Yuval Noah Harari");
    }

    @Test
    void haveNonNullUserEmailWhenBuildingWithValidData() {
        // given - a test Kindle highlight
        KindleHighLight highlight = createTestKindleHighlight();

        // then - verify userEmail is not null and has expected value
        assertThat(highlight.getUserEmail()).isNotNull();
        assertThat(highlight.getUserEmail()).isEqualTo("test@example.com");
    }
}

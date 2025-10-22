package com.duke.bookproject.book.service;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.account.service.UserService;
import com.duke.bookproject.book.model.KindleHighLight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

class KindleHighlightsParserTest {
    KindleHiglightsParser parser = new KindleHiglightsParser(null);

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
	void shouldParseBookTitleWithBOM() {
		String line = "\uFEFFnexus_yuval noah harari (Yuval Noah Harari)";

		String result = parser.parseTitle(line);

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
				"- Your Highlight on page xiii | Location 103-104 | Added on Monday, September 15, 2025 2:43:26 PM\n"
				+
				"\n" +
				"Power always stems from cooperation between large numbers of humans.";

		// WHEN
		KindleHighLight result = parser.parseSingleHighlight(highlightBlock);

		// THEN
		assertThat(result.getTitle()).isEqualTo("nexus_yuval noah harari");
		assertThat(result.getAuthor()).isEqualTo("Yuval Noah Harari");
		assertThat(result.getContent())
				.isEqualTo("Power always stems from cooperation between large numbers of humans.");
	}

	@Test
	void shouldParseMultipleHighlights() throws Exception {
		String filePath = "src/test/resources/kindleHighlightsSample.txt";
		String fileContent = Files.readString(Paths.get(filePath));

		List<KindleHighLight> results = parser.parseMultipleHighlights(fileContent);

		int expectedCount = fileContent.split("==========").length - 1;
		assertThat(results.size()).isEqualTo(expectedCount).isGreaterThan(1);
	}
}

package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.KindleHighLight;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KindleHiglightsParser {

	public String parseTitle(String line) {
		int indexOfOpenParentheses = line.indexOf('(');
		String title = line.substring(0, indexOfOpenParentheses).trim();

		if (title.startsWith("\uFEFF")) {
			title = title.substring(1);
		}

		return title;
	}

	public String parseAuthor(String line) {
		int indexOfOpenParentheses = line.indexOf('(');
		int indexOfCloseParentheses = line.indexOf(')');
		return line.substring(indexOfOpenParentheses + 1, indexOfCloseParentheses).trim();
	}

	public KindleHighLight parseSingleHighlight(String highlightBlock) {
		String[] lines = highlightBlock.split("\n");

		String author = parseAuthor(lines[0]);
		String title = parseTitle(lines[0]);

		String content = lines[3].trim();

		return KindleHighLight.builder()
				.title(title)
				.author(author)
				.content(content)
				.build();
	}

	public List<KindleHighLight> parseMultipleHighlights(String fileContent) {
		return Arrays.stream(fileContent.split("=========="))
				.map(String::trim)
				.filter(block -> !block.isEmpty())
				.map(this::parseSingleHighlight)
				.collect(Collectors.toList());
	}
}

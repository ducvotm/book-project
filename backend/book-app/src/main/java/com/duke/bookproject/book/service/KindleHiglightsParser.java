package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.KindleHighLight;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KindleHiglightsParser {
	String filePath = "src/test/resources/kindleHighlightsSample.txt";

	public String parseTitle(String line) {
		int indexOfOpenParentheses = line.indexOf('(');
		return line.substring(0, indexOfOpenParentheses).trim();
	}

	public String parseAuthor(String line) {
		int indexOfOpenParentheses = line.indexOf('(');
		int indexOfCloseParentheses = line.indexOf(')');
		return line.substring(indexOfOpenParentheses + 1, indexOfCloseParentheses).trim();
	}

	public KindleHighLight parseSingleHighlight(String highlightBlock) {
		// Splitting the block into lines
		String[] lines = highlightBlock.split("\n");

		// Extracting title and author from line 0
		String author = parseAuthor(lines[0]);
		String title = parseTitle(lines[0]);

		// Extracting the content from line 3
		String content = lines[3].trim();

		// Creating the KindleHighLight object
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

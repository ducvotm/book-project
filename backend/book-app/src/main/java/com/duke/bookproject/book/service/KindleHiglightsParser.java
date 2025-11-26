package com.duke.bookproject.book.service;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.account.service.UserService;
import com.duke.bookproject.book.model.KindleHighLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KindleHiglightsParser {
	private static final Logger logger = LoggerFactory.getLogger(KindleHiglightsParser.class);
	private final UserService userService;

	public KindleHiglightsParser(UserService userService) {
		this.userService = userService;
	}

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

		logger.debug("Attempting to get current user for highlight parsing");
		User currentUser = userService.getCurrentUser();
		if (currentUser == null) {
			logger.error("getCurrentUser() returned null");
			throw new IllegalStateException("Current user is null - cannot parse highlights without authenticated user");
		}

		String userEmail = currentUser.getEmail();
		if (userEmail == null || userEmail.isEmpty()) {
			logger.error("Current user email is null or empty. User ID: {}", currentUser.getId());
			throw new IllegalStateException(
					"Current user email is null or empty - cannot create highlight without user email");
		}

		logger.debug("Parsing highlight for user: {}", userEmail);

		return KindleHighLight.builder()
				.userEmail(userEmail)
				.author(author)
				.title(title)
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

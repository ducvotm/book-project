package com.duke.bookproject.book.controller;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.account.service.UserService;
import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.service.EmailService;
import com.duke.bookproject.book.service.KindleHighLightService;
import com.duke.bookproject.book.service.KindleHiglightsParser;
import com.duke.bookproject.constant.EmailConstant;
import com.duke.bookproject.template.EmailTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/kindle-highlights")
public class KindleHighLightController {

	private final KindleHighLightService service;
	private final KindleHiglightsParser parser;
	private final EmailService emailService;
	private final UserService userService;

	private static final String HIGHLIGHT_NOT_FOUND_ERROR_MESSAGE = "Could not find highlight with ID %d";

	public KindleHighLightController(KindleHighLightService service, KindleHiglightsParser parser,
			EmailService emailService, UserService userService) {
		this.service = service;
		this.parser = parser;
		this.emailService = emailService;
		this.userService = userService;
	}

	@GetMapping
	public List<KindleHighLight> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id:[0-9]+}")
	public KindleHighLight findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						String.format(HIGHLIGHT_NOT_FOUND_ERROR_MESSAGE, id)));
	}

	@PostMapping("/import")
	public ResponseEntity<String> importHighlights(@RequestBody String fileContent) {
		List<KindleHighLight> highlights = parser.parseMultipleHighlights(fileContent);
		service.saveAll(highlights);
		String message = "Imported " + highlights.size() + " highlights";
		return ResponseEntity.ok(message);
	}

	@DeleteMapping("/{id:[0-9]+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> delete(@PathVariable Long id) {
		Optional<KindleHighLight> highlightToDelete = service.findById(id);
		if (highlightToDelete.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(String.format(HIGHLIGHT_NOT_FOUND_ERROR_MESSAGE, id));
		}

		service.delete(highlightToDelete.get());
		return ResponseEntity.status(HttpStatus.OK).body("Deleted");
	}

	@PostMapping("/email")
	public ResponseEntity<String> emailHighlights() throws MessagingException {
		List<KindleHighLight> highlights = service.findAll();

		if (highlights.isEmpty()) {
			return ResponseEntity.ok("No highlights to email");
		}

		User currentUser = userService.getCurrentUser();
		String userEmail = currentUser.getEmail();
		String username = emailService.getUsernameFromEmail(userEmail);

		List<Map<String, String>> highlightMaps = new ArrayList<>();
		for (KindleHighLight highlight : highlights) {
			Map<String, String> highlightMap = new HashMap<>();
			highlightMap.put("title", highlight.getTitle());
			highlightMap.put("author", highlight.getAuthor());
			highlightMap.put("content", highlight.getContent());
			highlightMaps.add(highlightMap);
		}

		Map<String, Object> emailTemplate = EmailTemplate.getKindleHighlightsEmailTemplate(username,
				highlightMaps);
		emailService.sendMessageUsingThymeleafTemplate(userEmail, EmailConstant.KINDLE_HIGHLIGHTS_SUBJECT,
				emailTemplate);

		return ResponseEntity.ok("Email sent successfully");
	}
}

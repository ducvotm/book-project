package com.duke.bookproject.bookimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/books/import")
public class GoodreadsImportController {

	private static final Logger logger = LoggerFactory.getLogger(GoodreadsImportController.class);

	private final GoodreadsCsvParser csvParser;
	private final ImportService importService;

	public GoodreadsImportController(GoodreadsCsvParser csvParser, ImportService importService) {
		this.csvParser = csvParser;
		this.importService = importService;
	}

	@PostMapping(path = "/goodreads", consumes = "multipart/form-data")
	public ResponseEntity<String> importGoodreadsBooks(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body("File is empty");
		}

		try {
			List<GoodreadsBookImport> goodreadsBooks = csvParser.parseCsvFile(file.getInputStream());
			logger.info("Parsed {} books from CSV", goodreadsBooks.size());

			List<com.duke.bookproject.book.model.Book> savedBooks = importService.importGoodreadsBooks(goodreadsBooks);
			logger.info("Successfully saved {} books", savedBooks.size());

			String message = "Imported " + savedBooks.size() + " books";
			return ResponseEntity.ok(message);
		} catch (IOException e) {
			logger.error("Error parsing CSV file", e);
			return ResponseEntity.badRequest().body("Error parsing CSV file: " + e.getMessage());
		} catch (Exception e) {
			logger.error("Unexpected error during import", e);
			return ResponseEntity.status(500)
					.body("Error importing books: " + e.getClass().getSimpleName() + " - " + e.getMessage());
		}
	}
}

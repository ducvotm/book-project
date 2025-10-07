package com.duke.bookproject.book.controller;

import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.service.KindleHighLightService;
import com.duke.bookproject.book.service.KindleHiglightsParser;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kindle-highlights")
public class KindleHighLightController {

    private final KindleHighLightService service;
    private final KindleHiglightsParser parser;

    private static final String HIGHLIGHT_NOT_FOUND_ERROR_MESSAGE = "Could not find highlight with ID %d";

    public KindleHighLightController(KindleHighLightService service, KindleHiglightsParser parser) {
        this.service = service;
        this.parser = parser;
    }

    @GetMapping
    public List<KindleHighLight> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
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

    @DeleteMapping("/{id}")
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
}

package com.duke.bookproject.bookimport;

import com.duke.bookproject.shelf.model.PredefinedShelf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoodreadsBookImport {
	private String title;
	private String author;
	private String exclusiveShelf;

	public static Optional<PredefinedShelf.ShelfName> toPredefinedShelfName(String goodreadsShelf) {
		if (goodreadsShelf == null) {
			return Optional.empty();
		}

		switch (goodreadsShelf) {
			case "currently-reading":
				return Optional.of(PredefinedShelf.ShelfName.READING);
			case "read":
				return Optional.of(PredefinedShelf.ShelfName.READ);
			case "to-read":
				return Optional.of(PredefinedShelf.ShelfName.TO_READ);
			case "did-not-finish":
				return Optional.of(PredefinedShelf.ShelfName.DID_NOT_FINISH);
			default:
				return Optional.empty();
		}
	}
}

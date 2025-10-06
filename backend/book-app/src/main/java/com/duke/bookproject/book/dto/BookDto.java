/*
 * The book project lets a user keep track of different books they would like to read, are currently
 * reading, have read or did not finish.
 * Copyright (C) 2021  Karan Kumar
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.duke.bookproject.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.duke.bookproject.ExcludeFromJacocoGeneratedReport;
import com.duke.bookproject.book.model.Author;
import lombok.Data;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Data
@ExcludeFromJacocoGeneratedReport
public class BookDto {
  @Id
  @JsonProperty("id")
  private Long id;

  @JsonProperty("title")
  @NotNull(message = "Title is required")
  @NotBlank(message = "Title cannot be empty")
  private String title;

  @JsonProperty("numberOfPages")
  @Min(value = 1, message = "Number of pages must be at least 1")
  @Max(value = 23000, message = "Number of pages cannot exceed 23,000")
  private Integer numberOfPages;

  @JsonProperty("pagesRead")
  private Integer pagesRead;

  @JsonProperty("bookGenre")
  private String bookGenre; // currently need to use the constant names in request body (use "HORROR" vs
                            // "Horror")

  @JsonProperty("bookFormat")
  private String bookFormat; // currently need to use the constant names in request body (use "EBOOK" vs
                             // "eBook")

  @JsonProperty("seriesPosition")
  private Integer seriesPosition;

  @JsonProperty("edition")
  private Integer edition;

  @JsonProperty("bookRecommendedBy")
  private String bookRecommendedBy;

  @JsonProperty("isbn")
  private String isbn;

  @JsonProperty("yearofPublication")
  private Integer yearOfPublication;

  @JsonProperty("author")
  @NotNull(message = "Author is required")
  @Valid
  private Author author;

  @JsonProperty("predefinedShelf")
  @NotNull(message = "Predefined shelf is required")
  @Pattern(regexp = "To read|Reading|Read|Did not finish", message = "Invalid shelf. Valid options: To read, Reading, Read, Did not finish")
  private String predefinedShelf;

  @JsonProperty("bookReview")
  private String bookReview;
}

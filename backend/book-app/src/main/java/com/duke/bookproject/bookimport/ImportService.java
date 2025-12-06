/*
 * The book project lets a user keep track of different books they would like to read, are currently
 * reading, have read or did not finish.
 * Copyright (C) 2020  Karan Kumar
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

package com.duke.bookproject.bookimport;

import com.duke.bookproject.book.model.Author;
import com.duke.bookproject.book.model.Book;
import com.duke.bookproject.shelf.model.UserCreatedShelf;
import com.duke.bookproject.shelf.model.PredefinedShelf;
import com.duke.bookproject.book.model.RatingScale;
import com.duke.bookproject.book.service.BookService;
import com.duke.bookproject.shelf.service.PredefinedShelfService;
import com.duke.bookproject.shelf.service.UserCreatedShelfService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ImportService {
  private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
  private static final double GOODREADS_RATING_SCALE_FACTOR = 2;

  private final BookService bookService;
  private final PredefinedShelfService predefinedShelfService;
  private final UserCreatedShelfService userCreatedShelfService;

  public ImportService(
      BookService bookService,
      PredefinedShelfService predefinedShelfService,
      UserCreatedShelfService userCreatedShelfService) {
    this.bookService = bookService;
    this.predefinedShelfService = predefinedShelfService;
    this.userCreatedShelfService = userCreatedShelfService;
  }

  /**
   * Imports the books which are in Goodreads format
   *
   * @param goodreadsBookImports the books to import
   * @return the list of books saved successfully
   */
  public List<Book> importGoodreadsBooks(
      Collection<? extends GoodreadsBookImport> goodreadsBookImports) {
    if (CollectionUtils.isEmpty(goodreadsBookImports)) {
      logger.info("Goodreads imports is empty");
      return Collections.emptyList();
    }

    List<Book> books = toBooks(goodreadsBookImports);

    List<Book> savedBooks = books.stream()
        .map(bookService::save)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    savedBooks.forEach(b -> logger.info("Book: {} saved successfully", b));

    return savedBooks;
  }

  private List<Book> toBooks(Collection<? extends GoodreadsBookImport> goodreadsBookImports) {
    return goodreadsBookImports.stream()
        .map(this::toBook)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private Optional<Book> toBook(GoodreadsBookImport goodreadsBookImport) {
    if (StringUtils.isBlank(goodreadsBookImport.getTitle())) {
      logger.error("Title is blank for import: {}", goodreadsBookImport);
      return Optional.empty();
    }

    Optional<Author> author = toAuthor(goodreadsBookImport.getAuthor());
    if (author.isEmpty()) {
      logger.error("Author is null for import: {}", goodreadsBookImport);
      return Optional.empty();
    }

    String shelfSource = goodreadsBookImport.getExclusiveShelf();
    if (StringUtils.isBlank(shelfSource)) {
      shelfSource = goodreadsBookImport.getBookshelves();
    }
    Optional<PredefinedShelf> predefinedShelf = toPredefinedShelf(
        shelfSource,
        goodreadsBookImport.getDateRead(),
        GoodreadsBookImport::toPredefinedShelfName);
    if (predefinedShelf.isEmpty()) {
      logger.error("Predefined shelf is null for import: {}", goodreadsBookImport);
      return Optional.empty();
    }

    Book book = new Book(goodreadsBookImport.getTitle(), author.get(), predefinedShelf.get());

    String shelfSourceForCustom = goodreadsBookImport.getBookshelves();
    if (StringUtils.isBlank(shelfSourceForCustom)) {
      shelfSourceForCustom = goodreadsBookImport.getExclusiveShelf();
    }
    Optional<UserCreatedShelf> customShelf = toCustomShelf(
        shelfSourceForCustom, GoodreadsBookImport::toPredefinedShelfName);
    customShelf.ifPresent(book::setUserCreatedShelf);

    Optional<RatingScale> ratingScale = toRatingScale(goodreadsBookImport.getRating(),
        GOODREADS_RATING_SCALE_FACTOR);
    ratingScale.ifPresent(book::setRating);

    return Optional.of(book);
  }

  private Optional<Author> toAuthor(String name) {
    if (StringUtils.isBlank(name)) {
      return Optional.empty();
    }

    return Optional.of(new Author(name));
  }

  private Optional<PredefinedShelf> toPredefinedShelf(
      String shelves,
      LocalDate dateRead,
      Function<String, Optional<PredefinedShelf.ShelfName>> predefinedShelfNameMapper) {
    if (Objects.nonNull(dateRead)) {
      PredefinedShelf readShelf = predefinedShelfService.findReadShelf();
      return readShelf != null ? Optional.of(readShelf) : Optional.empty();
    }
    if (StringUtils.isBlank(shelves)) {
      return Optional.empty();
    }
    String[] shelvesArray = shelves.trim().split(",");

    return Arrays.stream(shelvesArray)
        .map(predefinedShelfNameMapper)
        .filter(Optional::isPresent)
        .findFirst()
        .flatMap(Function.identity())
        .flatMap(shelfName -> predefinedShelfService.findByPredefinedShelfNameAndLoggedInUser(shelfName));
  }

  private Optional<UserCreatedShelf> toCustomShelf(
      String shelves,
      Function<String, Optional<PredefinedShelf.ShelfName>> predefinedShelfNameMapper) {
    if (StringUtils.isBlank(shelves)) {
      return Optional.empty();
    }
    String[] shelvesArray = shelves.trim().split(",");

    Predicate<String> isNotPredefinedShelf = s -> predefinedShelfNameMapper.andThen(Optional::isEmpty)
        .apply(s);

    return Arrays.stream(shelvesArray)
        .filter(isNotPredefinedShelf)
        .findFirst()
        .map(String::trim)
        .map(userCreatedShelfService::findOrCreate);
  }

  private Optional<RatingScale> toRatingScale(Double ratingValue, double scaleFactor) {
    if (Objects.isNull(ratingValue)) {
      return Optional.of(RatingScale.NO_RATING);
    }
    return RatingScale.of(ratingValue * scaleFactor);
  }
}

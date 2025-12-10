/*
* The book project lets a user keep track of different books they would like to read, are currently
* reading, have read or did not finish.
* Copyright (C) 2020  Karan Kumar

* This program is free software: you can redistribute it and/or modify it under the terms of the
* GNU General Public License as published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.

* This program is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
* PURPOSE.  See the GNU General Public License for more details.

* You should have received a copy of the GNU General Public License along with this program.
* If not, see <https://www.gnu.org/licenses/>.
*/

package com.duke.bookproject.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.book.model.Book;
import com.duke.bookproject.book.model.ReadingLog;
import com.duke.bookproject.shelf.model.PredefinedShelf.ShelfName;

public interface ReadingLogRepository extends JpaRepository<ReadingLog, Long> {
	// Finding logs for streak calculation (by user, ordered by date)?
	// Finding a specific log entry (user + date + book)?
	@Query("SELECT l "
			+ "FROM ReadingLog l "
			+ "INNER JOIN FETCH b.author "
			+ "INNER JOIN FETCH b.predefinedShelf "
			+ "INNER JOIN FETCH b.tags "
			+ "INNER JOIN FETCH b.publishers")
	List<Book> findAllBooks(Pageable pageable);

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	List<Book> findAll();

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT b "
			+ "FROM Book b "
			+ "INNER JOIN FETCH b.author "
			+ "INNER JOIN FETCH b.predefinedShelf "
			+ "INNER JOIN FETCH b.tags "
			+ "INNER JOIN FETCH b.publishers "
			+ "WHERE b.id = :id")
	Optional<Book> findBookById(@Param("id") Long id);

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	List<Book> findByTitleContainingIgnoreCase(String title);
	// Finding logs by user and date range?

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT l "
			+ "FROM ReadingLog l "
			+ "INNER JOIN FETCH l.user AS a "
			+ "INNER JOIN FETCH l.date "
			+ "INNER JOIN FETCH b.tags "
			+ "INNER JOIN FETCH b.publishers "
			+ "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :titleOrAuthor, '%')) OR "
			+ "LOWER(a.fullName) LIKE LOWER(CONCAT('%', :titleOrAuthor, '%'))")
	List<Book> findByTitleOrAuthor(@Param("titleOrAuthor") String titleOrAuthor);

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT b "
			+ "FROM Book b "
			+ "INNER JOIN FETCH b.author "
			+ "INNER JOIN FETCH b.predefinedShelf s "
			+ "INNER JOIN FETCH b.tags "
			+ "INNER JOIN FETCH b.publishers "
			+ "WHERE s.predefinedShelfName = :predefinedShelfName")
	List<Book> findAllBooksByPredefinedShelfShelfName(
			@Param("predefinedShelfName") ShelfName predefinedShelfName);

	@EntityGraph(value = "Book.author", type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT b "
			+ "FROM Book b "
			+ "INNER JOIN FETCH b.author "
			+ "INNER JOIN FETCH b.predefinedShelf pds "
			+ "LEFT JOIN FETCH b.bookGenre "
			+ "LEFT JOIN FETCH b.publishers "
			+ "LEFT JOIN FETCH b.tags "
			+ "LEFT JOIN FETCH b.userCreatedShelf ucs "
			+ "WHERE pds.user = :user "
			+ "AND (ucs is NULL OR ucs.user = :user)")
	List<Book> findAllBooksForUser(@Param("user") User user);
}

/*
   The book project lets a user keep track of different books they would like to read, are currently
   reading, have read or did not finish.
   Copyright (C) 2020  Karan Kumar

   This program is free software: you can redistribute it and/or modify it under the terms of the
   GNU General Public License as published by the Free Software Foundation, either version 3 of the
   License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
   PURPOSE.  See the GNU General Public License for more details.

   You should have received a copy of the GNU General Public License along with this program.
   If not, see <https://www.gnu.org/licenses/>.
*/

package com.duke.bookproject.book.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duke.bookproject.account.model.User;
import com.duke.bookproject.book.dto.StatisticsDto;
import com.duke.bookproject.book.model.Book;
import com.duke.bookproject.book.model.ReadingLog;
import com.duke.bookproject.book.repository.ReadingLogRepository;
import com.duke.bookproject.shelf.model.PredefinedShelf.ShelfName;

import lombok.extern.java.Log;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
@Transactional
public class StatsService {

	private final BookService bookService;
	private final ReadingLogRepository readingLogRepository;

	public StatsService(
			BookService bookService,
			ReadingLogRepository readingLogRepository) {
		this.bookService = bookService;
		this.readingLogRepository = readingLogRepository;
	}

	public StatisticsDto getStatistics(User user) {
		LocalDate now = LocalDate.now();
		LocalDate startOfYear = now.withDayOfYear(1);
		LocalDate startOfMonth = now.withDayOfMonth(1);

		return StatisticsDto.builder()
				.allTime(calculatePeriodStatistics(user, null, null))
				.thisYear(calculatePeriodStatistics(user, startOfYear, now))
				.thisMonth(calculatePeriodStatistics(user, startOfMonth, now))
				.build();
	}

	private StatisticsDto.PeriodStatistics calculatePeriodStatistics(
			User user, LocalDate startDate, LocalDate endDate) {
		int booksRead = countBooksRead(user, startDate, endDate);
		int pagesRead = sumPagesRead(user, startDate, endDate);
		int currentStreak = calculateStreak(user, endDate != null ? endDate : LocalDate.now());

		return StatisticsDto.PeriodStatistics.builder()
				.booksRead(booksRead)
				.pagesRead(pagesRead)
				.currentStreak(currentStreak)
				.build();
	}

	
}

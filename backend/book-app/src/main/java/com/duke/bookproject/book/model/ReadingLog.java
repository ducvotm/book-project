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

package com.duke.bookproject.book.model;

import com.duke.bookproject.ExcludeFromJacocoGeneratedReport;
import com.duke.bookproject.account.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ExcludeFromJacocoGeneratedReport
public class ReadingLog {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Setter(AccessLevel.NONE)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "reading_log_user_id_fk"))
   @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
   @NotNull
   private User user;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "book_id", nullable = true, referencedColumnName = "id", foreignKey = @ForeignKey(name = "reading_log_book_id_fk"))
   @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
   private Book book;

   @NotNull
   private LocalDate date;

   @Min(value = 0, message = "Pages read must be non-negative")
   @NotNull
   private Integer pagesRead;
}

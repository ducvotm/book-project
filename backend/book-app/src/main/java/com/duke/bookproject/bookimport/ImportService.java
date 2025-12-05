package com.duke.bookproject.bookimport;

import com.duke.bookproject.book.model.Author;
import com.duke.bookproject.book.model.Book;
import com.duke.bookproject.book.service.BookService;
import com.duke.bookproject.shelf.model.PredefinedShelf;
import com.duke.bookproject.shelf.service.PredefinedShelfService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImportService {
   private final PredefinedShelfService predefinedShelfService;
   private final BookService bookService;

   public ImportService(PredefinedShelfService predefinedShelfService, BookService bookService) {
      this.predefinedShelfService = predefinedShelfService;
      this.bookService = bookService;
   }

   public List<Book> importGoodreadsBooks(List<GoodreadsBookImport> goodreadsBookImports) {
      List<Book> savedBooks = new ArrayList<>();

      for (GoodreadsBookImport data : goodreadsBookImports) {
         if (isValid(data)) {
            Book book = convertToBook(data);

            if (book != null) {
               Optional<Book> savedBook = bookService.save(book);

               if (savedBook.isPresent()) {
                  savedBooks.add(savedBook.get());
               }
            }
         }
      }

      return savedBooks;
   }

   private boolean isValid(GoodreadsBookImport data) {
      return data.getTitle() != null
            && !data.getTitle().trim().isEmpty()
            && data.getAuthor() != null
            && data.getExclusiveShelf() != null;
   }

   private Book convertToBook(GoodreadsBookImport data) {
      String title = data.getTitle();
      Author author = new Author(data.getAuthor());

      String goodreadsShelf = data.getExclusiveShelf();
      Optional<PredefinedShelf.ShelfName> shelfNameOpt = GoodreadsBookImport.toPredefinedShelfName(goodreadsShelf);

      if (shelfNameOpt.isEmpty()) {
         return null;
      }

      Optional<PredefinedShelf> predefinedShelfOpt = predefinedShelfService
            .findByPredefinedShelfNameAndLoggedInUser(shelfNameOpt.get());

      if (predefinedShelfOpt.isEmpty()) {
         return null;
      }

      PredefinedShelf predefinedShelf = predefinedShelfOpt.get();

      return new Book(title, author, predefinedShelf);
   }
}

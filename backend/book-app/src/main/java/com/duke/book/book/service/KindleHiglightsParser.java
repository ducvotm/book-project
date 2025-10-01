package com.duke.book.book.service;

import com.duke.book.book.model.KindleHighLight;

public class KindleHiglightsParser {
    public  KindleHighLight parseTitleAndAuthor(String line) {
// 1. Find where the "(" is
    Integer indexOfOpenParentheses = line.indexOf('(');
// 2. Everything before "(" = book title (but trim spaces!)
        String title = line.substring(0, indexOfOpenParentheses).trim();
// 3. Find where the ")" is
        Integer indexOfCloseParentheses = line.indexOf(')');
// 4. Everything between "(" and ")" = author
        String author = line.substring(indexOfOpenParentheses + 1, indexOfCloseParentheses).trim();
// 5. Create a KindleHighlight object
// 6. Set the book title and author
// 7. Return it
        return KindleHighLight.builder()
                .title(title)
                .author(author)
                .build();
    }
}

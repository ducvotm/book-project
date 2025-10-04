package com.duke.book.book.service;

import com.duke.book.book.model.KindleHighLight;

public class KindleHiglightsParser {

        public String parseTitle(String line) {
                int indexOfOpenParentheses = line.indexOf('(');
                return line.substring(0, indexOfOpenParentheses).trim();
        }

        public String parseAuthor(String line) {
                int indexOfOpenParentheses = line.indexOf('(');
                int indexOfCloseParentheses = line.indexOf(')');
                return line.substring(indexOfOpenParentheses + 1, indexOfCloseParentheses).trim();
        }

        public KindleHighLight parseSingleHighlight(String highlightBlock) {
                // Splitting the block into lines
                String[] lines = highlightBlock.split("\n");

                // Extracting title and author from line 0
                String author = parseAuthor(lines[0]);
                String title = parseTitle(lines[0]);

                // Extracting the content from line 3
                String content = lines[3].trim();

                // Creating the KindleHighLight object
                return KindleHighLight.builder()
                                .title(title)
                                .author(author)
                                .content(content)
                                .build();
        }
}

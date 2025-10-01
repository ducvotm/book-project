package com.duke.book.book.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KindleHighLight {
    @Id
    private String id;

    private String author;
    private String title;
    private String content;
    private Date publishDate;
}

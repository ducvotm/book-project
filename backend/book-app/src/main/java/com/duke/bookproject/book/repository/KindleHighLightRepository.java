package com.duke.bookproject.book.repository;

import com.duke.bookproject.book.model.KindleHighLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface KindleHighLightRepository extends JpaRepository<KindleHighLight, Long> {
}

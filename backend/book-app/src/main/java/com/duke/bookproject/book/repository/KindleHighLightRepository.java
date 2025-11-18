package com.duke.bookproject.book.repository;

import com.duke.bookproject.book.model.KindleHighLight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KindleHighLightRepository extends JpaRepository<KindleHighLight, Long> {
   @Query("SELECT h FROM KindleHighLight h WHERE h.userEmail = :userEmail")
   List<KindleHighLight> findByUserEmail(@Param("userEmail") String userEmail);

   @Query("SELECT h FROM KindleHighLight h WHERE h.id = :id AND h.userEmail = :userEmail")
   Optional<KindleHighLight> findByIdAndUserEmail(@Param("id") Long id, @Param("userEmail") String userEmail);
}

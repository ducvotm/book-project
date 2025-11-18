package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.repository.KindleHighLightRepository;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log
@Transactional
public class KindleHighLightService {
   private final KindleHighLightRepository repository;

   public KindleHighLightService(KindleHighLightRepository repository) {
      this.repository = repository;
   }

   public Optional<KindleHighLight> findById(@NonNull Long id) {
      return repository.findById(id);
   }

   public Optional<KindleHighLight> findByIdAndUserEmail(@NonNull Long id, @NonNull String userEmail) {
      return repository.findByIdAndUserEmail(id, userEmail);
   }

   public List<KindleHighLight> findAllByUserEmail(@NonNull String userEmail) {
      return repository.findByUserEmail(userEmail);
   }

   public void save(@NonNull KindleHighLight highlight) {
      repository.save(highlight);
   }

   public List<KindleHighLight> saveAll(@NonNull List<KindleHighLight> highlights) {
      return repository.saveAll(highlights);
   }

   public void delete(@NonNull KindleHighLight highlight) {
      repository.delete(highlight);
   }
}

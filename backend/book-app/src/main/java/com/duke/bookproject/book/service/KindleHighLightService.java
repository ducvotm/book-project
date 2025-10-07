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

   public List<KindleHighLight> findAll() {
      return repository.findAll();
   }

   public void save(@NonNull KindleHighLight highlight) {
      repository.save(highlight);
   }

   public void saveAll(@NonNull List<KindleHighLight> highlights) {
      repository.saveAll(highlights);
   }

   public void delete(@NonNull KindleHighLight highlight) {
      repository.delete(highlight);
   }

   public void deleteAll() {
      repository.deleteAll();
   }

   public Long count() {
      return repository.count();
   }
}

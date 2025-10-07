package com.duke.bookproject.book.repository;

import com.duke.bookproject.annotations.DataJpaIntegrationTest;
import com.duke.bookproject.book.model.KindleHighLight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaIntegrationTest
class KindleHighLightRepositoryTest {

   @Autowired
   private KindleHighLightRepository underTest;

   @BeforeEach
   void setUp() {
      underTest.deleteAll();
   }

   @Test
   void shouldSaveHighlight() {
      KindleHighLight highlight = KindleHighLight.builder()
            .title("Nexus")
            .author("Yuval Noah Harari")
            .content("Power always stems from cooperation between large numbers of humans.")
            .build();

      KindleHighLight saved = underTest.save(highlight);

      Optional<KindleHighLight> found = underTest.findById(saved.getId());

      assertThat(found).isPresent();
      assertThat(found.get().getTitle()).isEqualTo("Nexus");
      assertThat(found.get().getAuthor()).isEqualTo("Yuval Noah Harari");
      assertThat(found.get().getContent()).isEqualTo("Power always stems from cooperation between large numbers of humans.");
   }
}

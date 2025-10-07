package com.duke.bookproject.book.service;

import com.duke.bookproject.book.model.KindleHighLight;
import com.duke.bookproject.book.repository.KindleHighLightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KindleHighLightServiceTest {

   @Mock
   private KindleHighLightRepository repository;

   private KindleHighLightService service;

   @BeforeEach
   void setUp() {
      service = new KindleHighLightService(repository);
   }

   @Test
   void shouldSaveHighlight() {
      KindleHighLight highlight = KindleHighLight.builder()
            .title("Nexus")
            .author("Yuval Noah Harari")
            .content("Power always stems from cooperation between large numbers of humans.")
            .build();

      service.save(highlight);

      ArgumentCaptor<KindleHighLight> captor = ArgumentCaptor.forClass(KindleHighLight.class);
      verify(repository).save(captor.capture());

      KindleHighLight saved = captor.getValue();
      assertThat(saved).isEqualTo(highlight);
   }
}

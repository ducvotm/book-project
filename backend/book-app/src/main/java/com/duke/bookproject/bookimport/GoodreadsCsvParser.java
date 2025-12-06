package com.duke.bookproject.bookimport;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class GoodreadsCsvParser {

   private final CsvMapper csvMapper;

   public GoodreadsCsvParser() {
      this.csvMapper = new CsvMapper();
      this.csvMapper.registerModule(new JavaTimeModule());
   }

   public List<GoodreadsBookImport> parseCsvFile(InputStream csvInputStream) throws IOException {
      CsvSchema schema = CsvSchema.builder()
            .setUseHeader(true)
            .build();

      return csvMapper.readerFor(GoodreadsBookImport.class)
            .with(schema)
            .<GoodreadsBookImport>readValues(csvInputStream)
            .readAll();
   }
}

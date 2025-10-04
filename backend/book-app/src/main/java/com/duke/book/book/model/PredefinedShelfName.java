package com.duke.book.book.model;

import com.duke.book.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public enum PredefinedShelfName {
  TO_READ("To read"),
  READING("Reading"),
  READ("Read"),
  DID_NOT_FINISH("Did not finish");

  private final String name;

  PredefinedShelfName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}

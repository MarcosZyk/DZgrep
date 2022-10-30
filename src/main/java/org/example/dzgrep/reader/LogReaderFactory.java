package org.example.dzgrep.reader;

import java.io.IOException;

public class LogReaderFactory {

  private LogReaderFactory() {}

  public static LogReader createLogReader(String dirPath, String fileName) throws IOException {
    return new LogReaderImpl(dirPath, fileName);
  }
}

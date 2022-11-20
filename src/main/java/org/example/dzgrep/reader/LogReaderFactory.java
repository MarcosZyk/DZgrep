package org.example.dzgrep.reader;

import org.example.dzgrep.config.LogType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LogReaderFactory {

  private LogReaderFactory() {}

  public static LogReader createLogReader(String dirPath, String fileName) throws IOException {
    return new SingleTypeLogReader(dirPath, fileName);
  }

  public static LogReader createLogReader(String dirPath, Map<LogType, String> fileNameMap)
      throws IOException {
    Map<LogType, LogReader> singleTypeLogReaderMap = new HashMap<>();
    fileNameMap.forEach(
        (k, v) -> {
          try {
            singleTypeLogReaderMap.put(k, createLogReader(dirPath, v));
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
    return new CompositeLogReader(singleTypeLogReaderMap);
  }
}

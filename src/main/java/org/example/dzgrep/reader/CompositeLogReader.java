package org.example.dzgrep.reader;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class CompositeLogReader implements LogReader {

  private final Map<LogType, LogReader> singleTypeLogReaderMap;

  private final Map<LogType, LogRecord> nextRecordMap = new HashMap<>();

  private LogRecord nextElement;

  CompositeLogReader(Map<LogType, LogReader> singleTypeLogReaderMap) {
    this.singleTypeLogReaderMap = singleTypeLogReaderMap;
  }

  @Override
  public void close() throws Exception {
    for (LogReader logReader : singleTypeLogReaderMap.values()) {
      logReader.close();
    }
  }

  @Override
  public boolean hasNext() {
    if (nextElement == null) {
      readNext();
    }
    return nextElement != null;
  }

  @Override
  public LogRecord next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    LogRecord result = nextElement;
    nextElement = null;
    return result;
  }

  private void readNext() {
    LogType targetType = null;
    long lineNumber = -1;
    boolean hasLog = false;
    LogRecord logRecord;
    for (LogType logType : singleTypeLogReaderMap.keySet()) {
      logRecord = peek(logType);
      if (logRecord == null) {
        continue;
      }
      hasLog = true;
      if (lineNumber == -1 || logRecord.getLineNumber() < lineNumber) {
        lineNumber = logRecord.getLineNumber();
        targetType = logType;
      }
    }

    if (!hasLog) {
      return;
    }
    nextElement = pop(targetType);
    nextElement.setLogType(targetType);
  }

  private LogRecord peek(LogType logType) {
    LogRecord result = nextRecordMap.get(logType);
    if (result == null) {
      LogReader logReader = singleTypeLogReaderMap.get(logType);
      if (logReader.hasNext()) {
        result = logReader.next();
        nextRecordMap.put(logType, result);
      }
    }
    return result;
  }

  private LogRecord pop(LogType logType) {
    LogRecord result = nextRecordMap.get(logType);
    nextRecordMap.remove(logType);
    return result;
  }
}

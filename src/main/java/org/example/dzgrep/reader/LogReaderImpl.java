package org.example.dzgrep.reader;

import org.example.dzgrep.entity.LogRecord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LogReaderImpl implements LogReader {

  private Scanner scanner;

  private LogRecord nextRecord;

  private int currentIndex = 0;

  LogReaderImpl(String dirPath, String fileName) throws IOException {
    FileInputStream fileInputStream = new FileInputStream(dirPath + File.separator + fileName);
    scanner = new Scanner(new BufferedInputStream(fileInputStream));
  }

  @Override
  public void close() throws Exception {
    scanner.close();
  }

  @Override
  public boolean hasNext() {
    if (nextRecord == null) {
      readNext();
    }
    return nextRecord != null;
  }

  @Override
  public LogRecord next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    LogRecord result = nextRecord;
    nextRecord = null;
    return result;
  }

  private void readNext() {
    if (!scanner.hasNextLine()) {
      return;
    }

    LogRecord logRecord = new LogRecord();
    logRecord.setIndex(currentIndex);

    String text = scanner.nextLine();
    currentIndex += text.length() + 1;

    try {
      logRecord.parseFromText(text);
    } catch (ParseException e) {
      e.printStackTrace();
      return;
    }

    nextRecord = logRecord;
  }
}

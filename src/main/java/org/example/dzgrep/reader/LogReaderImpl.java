package org.example.dzgrep.reader;

import org.example.dzgrep.entity.LogRecord;
import org.example.dzgrep.util.TimeUtil;

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

  private int currentIndex = -1;

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
    logRecord.setIndex(++currentIndex);

    String text = scanner.nextLine();
    int fileNameSeparatorIndex = -1;
    if (text.startsWith("log")) {
      // file identifier
      fileNameSeparatorIndex = text.indexOf(":");
      logRecord.setFileName(text.substring(0, fileNameSeparatorIndex));
    }
    int timeSeparatorIndex = text.indexOf(",", fileNameSeparatorIndex);
    String time = text.substring(fileNameSeparatorIndex + 1, timeSeparatorIndex);

    try {
      logRecord.setTime(TimeUtil.parseTime(time));
    } catch (ParseException e) {
      e.printStackTrace();
      return;
    }

    int rollNumberSeparatorIndex = text.indexOf(" ", timeSeparatorIndex + 1);
    logRecord.setRollNumber(
        Integer.parseInt(text.substring(timeSeparatorIndex + 1, rollNumberSeparatorIndex)));

    int threadSeparatorIndex = text.indexOf(" ", rollNumberSeparatorIndex + 1);
    logRecord.setThread(text.substring(rollNumberSeparatorIndex + 1, threadSeparatorIndex));

    int typeSeparatorIndex = text.indexOf(" ", threadSeparatorIndex + 1);
    logRecord.setType(text.substring(threadSeparatorIndex + 1, typeSeparatorIndex));

    int sourceSeparatorIndex = text.indexOf(" ", typeSeparatorIndex + 1);
    logRecord.setSource(text.substring(typeSeparatorIndex + 1, sourceSeparatorIndex));

    // type - content
    logRecord.setContent(text.substring(sourceSeparatorIndex + 3));

    nextRecord = logRecord;
  }
}

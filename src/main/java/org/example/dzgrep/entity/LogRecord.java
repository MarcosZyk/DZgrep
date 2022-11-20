package org.example.dzgrep.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dzgrep.config.LogType;
import org.example.dzgrep.util.TimeUtil;

import java.text.ParseException;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogRecord {

  private String fileName;

  private long index;

  private Date time;

  private int rollNumber = -1;

  private String thread;

  private String type;

  private String source;

  private String content;

  private String rawText;

  private LogType logType;

  public LogRecord(String text) throws ParseException {
    parseFromText(text);
  }

  public void parseFromText(String text) throws ParseException {
    int fileNameSeparatorIndex = -1;
    if (text.startsWith("log")) {
      // file identifier
      fileNameSeparatorIndex = text.indexOf(":");
      this.fileName = text.substring(0, fileNameSeparatorIndex);
    }

    this.rawText = text.substring(fileNameSeparatorIndex + 1);
    if (!Character.isDigit(text.charAt(fileNameSeparatorIndex + 1))) {
      // not a log start with time
      content = rawText;
      return;
    }

    int timeSeparatorIndex = text.indexOf(",", fileNameSeparatorIndex);
    String time = text.substring(fileNameSeparatorIndex + 1, timeSeparatorIndex);

    this.time = TimeUtil.parseTime(time);

    int rollNumberSeparatorIndex = text.indexOf(" ", timeSeparatorIndex + 1);
    this.rollNumber =
        Integer.parseInt(text.substring(timeSeparatorIndex + 1, rollNumberSeparatorIndex));

    int threadSeparatorIndex = text.indexOf(" ", rollNumberSeparatorIndex + 1);
    this.thread = text.substring(rollNumberSeparatorIndex + 1, threadSeparatorIndex);

    int typeSeparatorIndex = text.indexOf(" ", threadSeparatorIndex + 1);
    this.type = text.substring(threadSeparatorIndex + 1, typeSeparatorIndex);

    int sourceSeparatorIndex = text.indexOf(" ", typeSeparatorIndex + 1);
    this.source = text.substring(typeSeparatorIndex + 1, sourceSeparatorIndex);

    // type - content
    this.content = text.substring(sourceSeparatorIndex + 3);
  }

  public String getRawText() {
    return rawText;
  }
}

package org.example.dzgrep.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogRecord {

  private String fileName;

  private long index;

  private Date time;

  private int rollNumber;

  private String thread;

  private String type;

  private String source;

  private String content;
}

package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogRecordView {

  private long index;

  private String thread;

  private String type;

  private String source;

  private String content;
}

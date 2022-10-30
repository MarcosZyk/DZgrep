package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TimeLogRecordView {

  private String time;

  private Map<String, LogRecordView> serverLogs;
}

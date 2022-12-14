package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogView {

  private String queryId;

  private List<String> serverList;

  private List<TimeLogRecordView> logList;
}

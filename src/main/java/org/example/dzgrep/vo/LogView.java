package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dzgrep.entity.LogRecord;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogView {

  private String queryId;

  private Map<String, List<LogRecord>> serverLog;
}

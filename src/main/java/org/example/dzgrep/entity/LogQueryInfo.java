package org.example.dzgrep.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dzgrep.config.LogType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogQueryInfo {

  private String queryId;

  private String queryName;

  private String startTime;

  private String endTime;

  private String keyword;

  private List<String> serverIpList;

  private List<LogType> typeList;
}

package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogQueryParam {

  private String queryName;

  private String startTime;

  private String endTime;

  private String keyword;

  private List<String> serverIpList;

  private List<String> typeList;
}

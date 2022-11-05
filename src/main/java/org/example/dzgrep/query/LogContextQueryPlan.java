package org.example.dzgrep.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dzgrep.entity.ServerInfo;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogContextQueryPlan {

  private ServerInfo targetServer;

  private String targetFile;

  private String rawContent;
}

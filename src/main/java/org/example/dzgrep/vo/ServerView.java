package org.example.dzgrep.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerView {

  private String ip;

  private String username;

  private String password;

  private String logDir;

  private boolean isActive;
}

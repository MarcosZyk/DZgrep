package org.example.dzgrep.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerInfo {

  private String ip;

  private String username;

  private String password;

  private String logDir;

  private boolean isActive;
}

package org.example.dzgrep.config;

import java.util.Locale;

public enum LogType {
  error,
  warn,
  info,
  debug,
  all;

  public String getTxtInFileName() {
    return name();
  }

  public String getTxtInFileContent() {
    return name().toUpperCase(Locale.ROOT);
  }
}

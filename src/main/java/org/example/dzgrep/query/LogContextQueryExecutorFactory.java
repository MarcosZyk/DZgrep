package org.example.dzgrep.query;

public class LogContextQueryExecutorFactory {

  private static final DZGrepContextExecutor executor = new DZGrepContextExecutor();

  private LogContextQueryExecutorFactory() {}

  public static LogContextQueryExecutor createExecutor() {
    return executor;
  }
}

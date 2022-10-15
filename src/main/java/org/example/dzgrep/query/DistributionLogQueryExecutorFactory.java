package org.example.dzgrep.query;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.ServerInfo;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DistributionLogQueryExecutorFactory {

  private DistributionLogQueryExecutorFactory() {}

  public static DistributionLogQueryExecutor createExecutor(
      List<ServerInfo> targetServerList,
      Map<String, Map<LogType, OutputStream>> serverResponseOutputStream) {
    return new DZGrepExecutor(targetServerList, serverResponseOutputStream);
  }
}

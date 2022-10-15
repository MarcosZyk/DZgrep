package org.example.dzgrep.query;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.ServerInfo;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DZGrepExecutor implements DistributionLogQueryExecutor {

  private static final int PORT = 22;
  private static final int TIME_OUT_LIMITATION = 20 * 60 * 1000;

  private static final String QUERY_TEMPLATE = "zgrep \"%s\" log*%s*";

  private final List<ServerInfo> targetServerList;
  private final Map<String, Map<LogType, OutputStream>> serverResponseOutputStream;

  DZGrepExecutor(
      List<ServerInfo> targetServerList,
      Map<String, Map<LogType, OutputStream>> serverResponseOutputStream) {
    this.targetServerList = targetServerList;
    this.serverResponseOutputStream = serverResponseOutputStream;
  }

  @Override
  public void execute(DistributionLogQueryPlan plan) throws Exception {
    for (ServerInfo serverInfo : targetServerList) {
      executeOnOneServer(serverInfo, plan);
    }
  }

  private void executeOnOneServer(ServerInfo serverInfo, DistributionLogQueryPlan plan)
      throws Exception {
    JSch jsch = new JSch();

    Session session = jsch.getSession(serverInfo.getUsername(), serverInfo.getIp(), PORT);
    session.setConfig("StrictHostKeyChecking", "no");
    session.setPassword(serverInfo.getPassword());
    session.setTimeout(TIME_OUT_LIMITATION);

    session.connect();

    Map<LogType, OutputStream> outputStreamMap = serverResponseOutputStream.get(serverInfo.getIp());
    for (LogType type : LogType.values()) {
      ChannelExec ec = (com.jcraft.jsch.ChannelExec) session.openChannel("exec");
      ec.setCommand(generateCommand(plan, type));
      ec.setInputStream(null);
      ec.setErrStream(System.err);
      OutputStream outputStream = outputStreamMap.get(type);
      ec.setOutputStream(outputStream);
      ec.connect();

      while (!ec.isClosed()) Thread.sleep(500);

      ec.disconnect();
      outputStream.close();
    }

    session.disconnect();
  }

  private String generateCommand(DistributionLogQueryPlan plan, LogType type) {
    return String.format(QUERY_TEMPLATE, plan.getKeyword(), type.getTxtInFileName());
  }
}

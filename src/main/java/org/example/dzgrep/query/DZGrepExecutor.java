package org.example.dzgrep.query;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.ServerInfo;
import org.example.dzgrep.util.TimeUtil;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DZGrepExecutor implements DistributionLogQueryExecutor {

  private static final int PORT = 22;
  private static final int TIME_OUT_LIMITATION = 20 * 60 * 1000;
  private static final String ACCESS_DIR_TEMPLATE = "cd %s";
  private static final String QUERY_TEMPLATE = "zgrep -E \"%s\" log*%s*";

  private final List<ServerInfo> targetServerList;
  private final Map<String, Map<LogType, OutputStream>> serverResponseOutputStream;
  private final ExecutorService executorService;

  DZGrepExecutor(
      List<ServerInfo> targetServerList,
      Map<String, Map<LogType, OutputStream>> serverResponseOutputStream) {
    this.targetServerList = targetServerList;
    this.serverResponseOutputStream = serverResponseOutputStream;
    this.executorService =
        Executors.newFixedThreadPool(targetServerList.size() * LogType.values().length);
  }

  @Override
  public void execute(DistributionLogQueryPlan plan) throws Exception {
    List<Future<?>> futureList = new ArrayList<>();
    for (ServerInfo serverInfo : targetServerList) {
      futureList.addAll(submitExecutionOnOneServer(serverInfo, plan));
    }
    for (Future<?> future : futureList) {
      future.get();
    }
  }

  @Override
  public void cancel() {
    executorService.shutdownNow();
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  private List<Future<?>> submitExecutionOnOneServer(
      ServerInfo serverInfo, DistributionLogQueryPlan plan) throws Exception {

    List<Future<?>> futureList = new ArrayList<>();
    Map<LogType, OutputStream> outputStreamMap = serverResponseOutputStream.get(serverInfo.getIp());
    for (LogType type : outputStreamMap.keySet()) {
      futureList.add(
          executorService.submit(
              () -> {
                try {

                  JSch jsch = new JSch();

                  Session session =
                      jsch.getSession(serverInfo.getUsername(), serverInfo.getIp(), PORT);
                  session.setConfig("StrictHostKeyChecking", "no");
                  session.setPassword(serverInfo.getPassword());
                  session.setTimeout(TIME_OUT_LIMITATION);

                  session.connect();

                  long currentTime = System.currentTimeMillis();
                  System.out.println(
                      "Start retrieving " + type.name() + " logs from " + serverInfo.getIp());
                  OutputStream outputStream = outputStreamMap.get(type);
                  executeCommand(
                      session, generateCommand(plan, serverInfo.getLogDir(), type), outputStream);
                  outputStream.close();
                  System.out.println(
                      "Finish retrieving "
                          + type.name()
                          + " logs from "
                          + serverInfo.getIp()
                          + " in "
                          + (System.currentTimeMillis() - currentTime)
                          + "ms");

                  session.disconnect();
                } catch (Exception e) {
                  System.out.println(
                      "Failed retrieving " + type.name() + " logs from " + serverInfo.getIp());
                  e.printStackTrace();
                }
              }));
    }
    return futureList;
  }

  private void executeCommand(Session session, String command, OutputStream outputStream)
      throws Exception {
    System.out.println("Log query command: " + command);
    ChannelExec ec = (com.jcraft.jsch.ChannelExec) session.openChannel("exec");
    ec.setCommand(command);
    ec.setInputStream(null);
    ec.setErrStream(System.err);
    OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
    ec.setOutputStream(outputStream);
    ec.connect();

    while (!ec.isClosed()) Thread.sleep(500);

    ec.disconnect();
    bufferedOutputStream.flush();
  }

  private String generateCommand(DistributionLogQueryPlan plan, String logDir, LogType type) {
    return String.format(ACCESS_DIR_TEMPLATE, logDir)
        + "\n"
        + String.format(
            QUERY_TEMPLATE,
            "("
                + TimeUtil.parseTimeRangeToRegex(plan.getStartTime(), plan.getEndTime())
                + ").*("
                + plan.getKeyword()
                + ")",
            type.getTxtInFileName())
        + "\n";
  }
}

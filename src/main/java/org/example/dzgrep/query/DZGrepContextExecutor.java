package org.example.dzgrep.query;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.example.dzgrep.entity.ServerInfo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class DZGrepContextExecutor implements LogContextQueryExecutor {

  private static final int PORT = 22;
  private static final int TIME_OUT_LIMITATION = 20 * 60 * 1000;
  private static final String ACCESS_DIR_TEMPLATE = "cd %s";
  private static final String QUERY_TEMPLATE = "zgrep -A %d \"%s\" %s";
  private static final int CONTEXT_RANGE = 50;

  DZGrepContextExecutor() {}

  @Override
  public String execute(LogContextQueryPlan plan) throws Exception {
    long currentTime = System.currentTimeMillis();
    try {
      System.out.println("Start retrieving log context from " + plan.getTargetServer().getIp());
      String result = executeOnServer(plan);
      System.out.println(
          "Finish retrieving log context from "
              + plan.getTargetServer().getIp()
              + " in "
              + (System.currentTimeMillis() - currentTime)
              + "ms");
      return result;
    } catch (Exception e) {
      System.out.println(
          "Failed retrieving log context from "
              + plan.getTargetServer().getIp()
              + " in "
              + (System.currentTimeMillis() - currentTime)
              + "ms");
      e.printStackTrace();
      throw e;
    }
  }

  private String executeOnServer(LogContextQueryPlan plan) throws Exception {
    ServerInfo serverInfo = plan.getTargetServer();

    JSch jsch = new JSch();

    Session session = jsch.getSession(serverInfo.getUsername(), serverInfo.getIp(), PORT);
    session.setConfig("StrictHostKeyChecking", "no");
    session.setPassword(serverInfo.getPassword());
    session.setTimeout(TIME_OUT_LIMITATION);

    session.connect();

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    executeCommand(session, generateCommand(plan, serverInfo.getLogDir()), byteArrayOutputStream);

    session.disconnect();

    return byteArrayOutputStream.toString();
  }

  private void executeCommand(Session session, String command, OutputStream outputStream)
      throws Exception {
    System.out.println("Log Context query command: " + command);
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

  private String generateCommand(LogContextQueryPlan plan, String logDir) {
    return String.format(ACCESS_DIR_TEMPLATE, logDir)
        + "\n"
        + String.format(
            QUERY_TEMPLATE,
            CONTEXT_RANGE,
            plan.getRawContent().replace("[", "\\[").replace("]", "\\]"),
            plan.getTargetFile())
        + "\n";
  }
}

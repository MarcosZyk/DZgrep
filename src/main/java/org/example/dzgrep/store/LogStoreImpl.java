package org.example.dzgrep.store;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogStoreImpl implements LogStore {

  private static final String ROOT_DIR_NAME = "dzgrep";
  private static final String ROOT_DIR_PATH = "." + File.separator + ROOT_DIR_NAME;
  private static final String LOG_FILE_TEMPLATE = "log_%s.log";

  @Override
  public Map<String, Map<LogType, OutputStream>> getLogStoreOutputStream(LogQueryInfo queryInfo)
      throws IOException {
    String queryDirPath = ROOT_DIR_PATH + File.separator + queryInfo.getQueryId();
    File queryDir = new File(queryDirPath);
    queryDir.mkdir();
    Map<String, Map<LogType, OutputStream>> result = new HashMap<>();
    for (String serverIp : queryInfo.getServerIpList()) {
      String serverDirPath = queryDirPath + File.separator + serverIp;
      File serverDir = new File(serverDirPath);
      serverDir.mkdir();
      Map<LogType, OutputStream> logFileOutputStreamMap = new HashMap<>();
      for (LogType type : LogType.values()) {
        String filePath =
            serverDirPath
                + File.separator
                + String.format(LOG_FILE_TEMPLATE, type.getTxtInFileName());
        File logFile = new File(filePath);
        FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
        logFileOutputStreamMap.put(type, fileOutputStream);
      }
      result.put(serverIp, logFileOutputStreamMap);
    }

    return result;
  }

  @Override
  public Map<String, InputStream> getAllLogInputStream(String queryId) throws IOException {
    String queryDirPath = ROOT_DIR_PATH + File.separator + queryId;
    File queryDir = new File(queryDirPath);
    Map<String, InputStream> result = new HashMap<>();
    for (File serverDir : queryDir.listFiles()) {
      result.put(
          serverDir.getName(),
          new FileInputStream(
              new File(
                  serverDir.getPath()
                      + File.separator
                      + String.format(LOG_FILE_TEMPLATE, LogType.all.getTxtInFileName()))));
    }
    return null;
  }
}

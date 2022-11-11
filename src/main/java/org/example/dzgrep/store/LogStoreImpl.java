package org.example.dzgrep.store;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.example.dzgrep.entity.LogRecord;
import org.example.dzgrep.reader.LogReader;
import org.example.dzgrep.reader.LogReaderFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class LogStoreImpl implements LogStore {

  private static final String ROOT_DIR_NAME = "dzgrep";
  private static final String LOG_FILE_TEMPLATE = "log_%s.log";
  private static final String HISTORY_DIR_NAME = "history";
  private static final String HISTORY_DIR_TMP_NAME = "history_tmp";

  private String rootDirPath;

  public LogStoreImpl() {
    String rootPath = getRootDirPath();
    File rootDir = new File(rootPath);
    if (!rootDir.exists()) {
      return;
    }
    String historyTmpDirPath = rootPath + File.separator + HISTORY_DIR_TMP_NAME;
    File historyTmpDir = new File(historyTmpDirPath);
    historyTmpDir.mkdir();
    for (File dir : rootDir.listFiles()) {
      if (dir.getName().equals(HISTORY_DIR_TMP_NAME)) {
        continue;
      }
      try {
        Files.move(
            Paths.get(dir.getAbsolutePath()),
            Paths.get(historyTmpDirPath + File.separator + dir.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    historyTmpDir.renameTo(new File(rootPath + File.separator + HISTORY_DIR_NAME));
  }

  @Override
  public Map<String, Map<LogType, OutputStream>> getLogStoreOutputStream(LogQueryInfo queryInfo)
      throws IOException {
    String queryDirPath = getRootDirPath() + File.separator + queryInfo.getQueryId();
    File queryDir = new File(queryDirPath);
    queryDir.mkdirs();
    Map<String, Map<LogType, OutputStream>> result = new HashMap<>();
    for (String serverIp : queryInfo.getServerIpList()) {
      String serverDirPath = queryDirPath + File.separator + serverIp;
      File serverDir = new File(serverDirPath);
      serverDir.mkdirs();
      Map<LogType, OutputStream> logFileOutputStreamMap = new HashMap<>();
      for (LogType type : LogType.values()) {
        String filePath =
            serverDirPath
                + File.separator
                + String.format(LOG_FILE_TEMPLATE, type.getTxtInFileName());
        File logFile = new File(filePath);
        Files.createFile(logFile.toPath());
        FileOutputStream fileOutputStream = new FileOutputStream(logFile, true);
        logFileOutputStreamMap.put(type, fileOutputStream);
      }
      result.put(serverIp, logFileOutputStreamMap);
    }

    return result;
  }

  @Override
  public void pruneEmptyServer(String queryId) throws IOException {
    String queryDirPath = getRootDirPath() + File.separator + queryId;
    File queryDir = new File(queryDirPath);
    for (File serverDir : queryDir.listFiles()) {
      if (!serverDir.isDirectory()) {
        continue;
      }

      boolean canSkip = false;
      for (File logFile : serverDir.listFiles()) {
        if (logFile.length() > 0) {
          canSkip = true;
          break;
        }
      }
      if (canSkip) {
        continue;
      }
      for (File logFile : serverDir.listFiles()) {
        logFile.delete();
      }
      serverDir.delete();
    }
  }

  @Override
  public Map<String, LogReader> getAllLogReader(String queryId) throws IOException {
    String queryDirPath = getRootDirPath() + File.separator + queryId;
    File queryDir = new File(queryDirPath);
    Map<String, LogReader> result = new HashMap<>();
    for (File serverDir : queryDir.listFiles()) {
      if (!serverDir.isDirectory()) {
        continue;
      }
      result.put(
          serverDir.getName(),
          LogReaderFactory.createLogReader(
              serverDir.getPath(),
              String.format(LOG_FILE_TEMPLATE, LogType.all.getTxtInFileName())));
    }
    return result;
  }

  @Override
  public LogRecord getRawLog(String queryId, String server, long index) throws IOException {
    String logFilePath =
        getRootDirPath()
            + File.separator
            + queryId
            + File.separator
            + server
            + File.separator
            + String.format(LOG_FILE_TEMPLATE, LogType.all.getTxtInFileName());
    FileInputStream fileInputStream = new FileInputStream(logFilePath);
    FileChannel fileChannel = fileInputStream.getChannel();
    ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
    fileChannel.read(buffer, index);
    Scanner scanner = new Scanner(new ByteArrayInputStream(buffer.array()));
    if (scanner.hasNextLine()) {
      try {
        return new LogRecord(scanner.nextLine());
      } catch (ParseException e) {
        e.printStackTrace();
        return null;
      }
    } else {
      return null;
    }
  }

  private String getRootDirPath() {
    if (rootDirPath == null) {
      ApplicationHome ah = new ApplicationHome(getClass());
      File file = ah.getSource();
      rootDirPath = file.getParentFile().getAbsolutePath() + File.separator + ROOT_DIR_NAME;
    }
    return rootDirPath;
  }
}

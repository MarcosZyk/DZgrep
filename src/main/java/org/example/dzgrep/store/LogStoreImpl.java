package org.example.dzgrep.store;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.example.dzgrep.entity.LogRecord;
import org.example.dzgrep.reader.LogReader;
import org.example.dzgrep.reader.LogReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Component
public class LogStoreImpl implements LogStore {

  private static final String ROOT_DIR_NAME = "dzgrep";
  private static final String LOG_FILE_TEMPLATE = "log_%s.log";

  private String rootDirPath;

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
      try {
        rootDirPath = ResourceUtils.getURL("classpath:").getPath() + File.separator + ROOT_DIR_NAME;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return rootDirPath;
  }
}

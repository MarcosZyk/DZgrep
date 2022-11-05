package org.example.dzgrep.store;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.example.dzgrep.entity.LogRecord;
import org.example.dzgrep.reader.LogReader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface LogStore {

  Map<String, Map<LogType, OutputStream>> getLogStoreOutputStream(LogQueryInfo queryInfo)
      throws IOException;

  void pruneEmptyServer(String queryId) throws IOException;

  Map<String, LogReader> getAllLogReader(String queryId) throws IOException;

  LogRecord getRawLog(String queryId, String server, long index) throws IOException;
}

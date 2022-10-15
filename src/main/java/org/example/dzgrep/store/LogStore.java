package org.example.dzgrep.store;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface LogStore {

  Map<String, Map<LogType, OutputStream>> getLogStoreOutputStream(LogQueryInfo queryInfo)
      throws IOException;

  Map<String, InputStream> getAllLogInputStream(String queryId) throws IOException;
}

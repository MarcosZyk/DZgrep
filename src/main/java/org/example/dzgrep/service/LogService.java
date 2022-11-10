package org.example.dzgrep.service;

import org.example.dzgrep.vo.LogContextParam;
import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogView;

public interface LogService {

  LogView createLogQuery(LogQueryParam logQueryParam) throws Exception;

  void cancelLogQuery(String queryId);

  LogView getExistingLogQuery(String queryId);

  String getLogContext(LogContextParam logContextParam) throws Exception;

  LogView getNextPage(String queryId) throws Exception;
}

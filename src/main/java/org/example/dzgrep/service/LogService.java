package org.example.dzgrep.service;

import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogView;

public interface LogService {

  LogView createLogQuery(LogQueryParam logQueryParam);

  LogView getExistingLogQuery(String queryId);
}

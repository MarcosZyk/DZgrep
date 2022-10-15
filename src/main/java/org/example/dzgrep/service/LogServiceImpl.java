package org.example.dzgrep.service;

import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogView;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {
  @Override
  public LogView createLogQuery(LogQueryParam logQueryParam) {
    return null;
  }

  @Override
  public LogView getExistingLogQuery(String queryId) {
    return null;
  }
}

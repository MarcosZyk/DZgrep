package org.example.dzgrep.service;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.example.dzgrep.query.DistributionLogQueryExecutor;
import org.example.dzgrep.query.DistributionLogQueryExecutorFactory;
import org.example.dzgrep.query.DistributionLogQueryPlan;
import org.example.dzgrep.store.LogStore;
import org.example.dzgrep.store.ServerStore;
import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogView;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LogServiceImpl implements LogService {

  private static final AtomicInteger QUERY_ID_GENERATOR = new AtomicInteger();
  private static final String QUERY_ID_TEMPLATE = "query_%s";

  private final ServerStore serverStore;
  private final LogStore logStore;

  public LogServiceImpl(ServerStore serverStore, LogStore logStore) {
    this.serverStore = serverStore;
    this.logStore = logStore;
  }

  private static String generateQueryId() {
    return String.format(QUERY_ID_TEMPLATE, QUERY_ID_GENERATOR.getAndIncrement());
  }

  @Override
  public LogView createLogQuery(LogQueryParam logQueryParam) throws Exception {
    String queryId = generateQueryId();
    Map<String, Map<LogType, OutputStream>> outputStreamMap =
        logStore.getLogStoreOutputStream(generateLogQueryInfo(logQueryParam, queryId));
    DistributionLogQueryExecutor executor =
        DistributionLogQueryExecutorFactory.createExecutor(
            serverStore.getServerList(logQueryParam.getServerIpList()), outputStreamMap);
    executor.execute(generateDistributionLogQueryPlan(logQueryParam));

    return new LogView(queryId, Collections.emptyMap());
  }

  @Override
  public LogView getExistingLogQuery(String queryId) {
    return null;
  }

  private LogQueryInfo generateLogQueryInfo(LogQueryParam logQueryParam, String queryId) {
    return new LogQueryInfo(
        queryId,
        logQueryParam.getQueryName(),
        logQueryParam.getStartTime(),
        logQueryParam.getEndTime(),
        logQueryParam.getKeyword(),
        logQueryParam.getServerIpList());
  }

  private DistributionLogQueryPlan generateDistributionLogQueryPlan(LogQueryParam logQueryParam) {
    return new DistributionLogQueryPlan(
        logQueryParam.getStartTime(), logQueryParam.getEndTime(), logQueryParam.getKeyword());
  }
}

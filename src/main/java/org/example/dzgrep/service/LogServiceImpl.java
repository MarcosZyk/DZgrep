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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {

  private static final AtomicInteger QUERY_ID_GENERATOR = new AtomicInteger();
  private static final String QUERY_ID_TEMPLATE = "query_%s";

  private final ServerStore serverStore;
  private final LogStore logStore;

  private final Map<String, DistributionLogQueryExecutor> executorMap = new ConcurrentHashMap<>();

  public LogServiceImpl(ServerStore serverStore, LogStore logStore) {
    this.serverStore = serverStore;
    this.logStore = logStore;
  }

  private static String generateQueryId() {
    return String.format(QUERY_ID_TEMPLATE, QUERY_ID_GENERATOR.getAndIncrement());
  }

  @Override
  public LogView createLogQuery(LogQueryParam logQueryParam) throws Exception {
    cleanLogQueryParam(logQueryParam);
    String queryId = generateQueryId();
    Map<String, Map<LogType, OutputStream>> outputStreamMap =
        logStore.getLogStoreOutputStream(generateLogQueryInfo(logQueryParam, queryId));
    DistributionLogQueryExecutor executor =
        DistributionLogQueryExecutorFactory.createExecutor(
            serverStore.getServerList(logQueryParam.getServerIpList()), outputStreamMap);

    executorMap.put(queryId, executor);
    try {
      executor.execute(generateDistributionLogQueryPlan(logQueryParam));
    } catch (Exception e) {
      if (executor.isCancelled()) {
        throw new Exception("Query cancelled");
      } else {
        throw e;
      }
    } finally {
      executorMap.remove(queryId);
    }

    logStore.pruneEmptyServer(queryId);

    return new LogView(queryId, Collections.emptyMap());
  }

  @Override
  public void cancelLogQuery(String queryId) {
    executorMap.get(queryId).cancel();
  }

  @Override
  public LogView getExistingLogQuery(String queryId) {
    return null;
  }

  private void cleanLogQueryParam(LogQueryParam logQueryParam) {
    logQueryParam.setQueryName(logQueryParam.getQueryName().trim());
    logQueryParam.setStartTime(logQueryParam.getStartTime().trim());
    logQueryParam.setEndTime(logQueryParam.getEndTime().trim());
    logQueryParam.setKeyword(logQueryParam.getKeyword().trim());
    logQueryParam.setServerIpList(
        logQueryParam.getServerIpList().stream().map(String::trim).collect(Collectors.toList()));
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

package org.example.dzgrep.service;

import org.example.dzgrep.config.LogType;
import org.example.dzgrep.entity.LogQueryInfo;
import org.example.dzgrep.entity.LogRecord;
import org.example.dzgrep.entity.ServerInfo;
import org.example.dzgrep.query.DistributionLogQueryExecutor;
import org.example.dzgrep.query.DistributionLogQueryExecutorFactory;
import org.example.dzgrep.query.DistributionLogQueryPlan;
import org.example.dzgrep.query.LogContextQueryExecutor;
import org.example.dzgrep.query.LogContextQueryExecutorFactory;
import org.example.dzgrep.query.LogContextQueryPlan;
import org.example.dzgrep.reader.LogReader;
import org.example.dzgrep.store.LogStore;
import org.example.dzgrep.store.ServerStore;
import org.example.dzgrep.util.TimeUtil;
import org.example.dzgrep.vo.LogContextParam;
import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogRecordView;
import org.example.dzgrep.vo.LogView;
import org.example.dzgrep.vo.TimeLogRecordView;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {

  private static final AtomicInteger QUERY_ID_GENERATOR = new AtomicInteger();
  private static final String QUERY_ID_TEMPLATE = "query_%s";
  private static final int PAGE_SIZE = 50;

  private final ServerStore serverStore;
  private final LogStore logStore;

  private final Map<String, DistributionLogQueryExecutor> executorMap = new ConcurrentHashMap<>();

  private final Map<String, QueryResultGenerator> queryResultGeneratorMap =
      new ConcurrentHashMap<>();

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

    QueryResultGenerator resultGenerator =
        new QueryResultGenerator(logStore.getAllLogReader(queryId));
    queryResultGeneratorMap.put(queryId, resultGenerator);

    return getNextPage(queryId);
  }

  private LogView generateNextPage(String queryId, QueryResultGenerator resultGenerator) {
    List<TimeLogRecordView> resultList = new ArrayList<>(PAGE_SIZE);
    for (int i = 0; i < PAGE_SIZE; i++) {
      if (resultGenerator.hasNext()) {
        resultList.add(resultGenerator.next());
      } else {
        break;
      }
    }

    return new LogView(queryId, resultGenerator.getServerList(), resultList);
  }

  @Override
  public void cancelLogQuery(String queryId) {
    executorMap.get(queryId).cancel();
  }

  @Override
  public LogView getExistingLogQuery(String queryId) {
    return null;
  }

  @Override
  public String getLogContext(LogContextParam logContextParam) throws Exception {
    LogRecord logRecord =
        logStore.getRawLog(
            logContextParam.getQueryId(),
            logContextParam.getTargetServer(),
            logContextParam.getIndex());
    ServerInfo serverInfo = serverStore.getServer(logContextParam.getTargetServer());
    LogContextQueryExecutor executor = LogContextQueryExecutorFactory.createExecutor();
    return executor.execute(
        new LogContextQueryPlan(serverInfo, logRecord.getFileName(), logRecord.getRawText()));
  }

  @Override
  public LogView getNextPage(String queryId) throws Exception {
    return generateNextPage(queryId, queryResultGeneratorMap.get(queryId));
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

  private static class QueryResultGenerator implements Iterator<TimeLogRecordView> {

    private final Map<String, LogReader> serverLogReader;
    private final List<String> serverList;

    private final Map<String, LogRecord> nextRecordMap = new HashMap<>();

    private TimeLogRecordView nextElement;

    QueryResultGenerator(Map<String, LogReader> serverLogReader) {
      this.serverLogReader = serverLogReader;
      this.serverList = new ArrayList<>(serverLogReader.keySet());
    }

    List<String> getServerList() {
      return serverList;
    }

    @Override
    public boolean hasNext() {
      if (nextElement == null) {
        readNext();
      }
      return nextElement != null;
    }

    @Override
    public TimeLogRecordView next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      TimeLogRecordView result = nextElement;
      nextElement = null;
      return result;
    }

    private void readNext() {
      Map<String, LogRecordView> result = new HashMap<>();
      Date currentDate = null;
      LogRecord logRecord;
      for (String server : serverList) {
        logRecord = peek(server);
        if (logRecord == null) {
          continue;
        }
        if (currentDate == null || logRecord.getTime().before(currentDate)) {
          currentDate = logRecord.getTime();
        }
      }

      if (currentDate == null) {
        return;
      }

      for (String server : serverList) {
        logRecord = peek(server);
        if (logRecord == null) {
          continue;
        }
        if (logRecord.getTime().equals(currentDate)) {
          result.put(server, generateLogRecordView(pop(server)));
        }
      }
      nextElement = new TimeLogRecordView(TimeUtil.formatTime(currentDate), result);
    }

    private LogRecord peek(String server) {
      LogRecord result = nextRecordMap.get(server);
      if (result == null) {
        LogReader logReader = serverLogReader.get(server);
        if (logReader.hasNext()) {
          result = logReader.next();
          nextRecordMap.put(server, result);
        }
      }
      return result;
    }

    private LogRecord pop(String server) {
      LogRecord result = nextRecordMap.get(server);
      nextRecordMap.remove(server);
      return result;
    }

    private LogRecordView generateLogRecordView(LogRecord logRecord) {
      return new LogRecordView(
          logRecord.getIndex(),
          logRecord.getThread(),
          logRecord.getType(),
          logRecord.getSource(),
          logRecord.getContent());
    }
  }
}

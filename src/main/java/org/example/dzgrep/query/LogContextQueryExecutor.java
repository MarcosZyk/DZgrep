package org.example.dzgrep.query;

public interface LogContextQueryExecutor {

  String execute(LogContextQueryPlan plan) throws Exception;
}

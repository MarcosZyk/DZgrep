package org.example.dzgrep.query;

public interface DistributionLogQueryExecutor {

  void execute(DistributionLogQueryPlan plan) throws Exception;
}

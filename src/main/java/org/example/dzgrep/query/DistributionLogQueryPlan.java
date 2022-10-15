package org.example.dzgrep.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DistributionLogQueryPlan {

  private String startTime;

  private String endTime;

  private String keyword;
}

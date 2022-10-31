package org.example.dzgrep;

import org.example.dzgrep.util.TimeUtil;
import org.junit.jupiter.api.Test;

public class TimeUtilTest {

  @Test
  public void testParseTimeRangeToRegex() {
    String startTime = "2022-10-22 09:28:31";
    String endTime = "2022-10-22 09:53:54";
    for (String element : TimeUtil.parseTimeRangeToRegex(startTime, endTime).split("\\|")) {
      System.out.println(element);
    }

    startTime = "2022-10-22 09:28:31";
    endTime = "2022-10-22 09:53:54";
    for (String element : TimeUtil.parseTimeRangeToRegex(startTime, endTime).split("\\|")) {
      System.out.println(element);
    }
  }
}

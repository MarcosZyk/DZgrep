package org.example.dzgrep;

import org.example.dzgrep.service.LogService;
import org.example.dzgrep.service.ServerService;
import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.ServerView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class DZGrepTestExample {

  @Autowired private LogService logService;

  @Autowired private ServerService serverService;

  @Test
  public void testDZGrep() throws Exception {

    String[] ipList =
        new String[] {
          "172.20.70.2",
          "172.20.70.3",
          "172.20.70.4",
          "172.20.70.5",
          "172.20.70.13",
          "172.20.70.14",
          "172.20.70.16",
          "172.20.70.18",
          "172.20.70.19"
        };
    for (String ip : ipList) {
      serverService.registerServer(
          new ServerView(
              ip, "user", "pwd", "/data/iotdb/master_0929_1061156/datanode/logs_iotdb_4563", true));
    }

    // the fetched logs are stored in the dzgrep dir of target/test-classes
    logService.createLogQuery(
        new LogQueryParam(
            "meta",
            "2022-09-29 20:23:38",
            "2022-09-29 20:23:38",
            "MetaData error",
            Arrays.asList(ipList),
            Arrays.asList("error", "warn", "info", "debug")));
    //
    //    logService.createLogQuery(
    //        new LogQueryParam("ratis", "", "", "RatisRequestFailedException",
    // Arrays.asList(ipList)));
  }
}

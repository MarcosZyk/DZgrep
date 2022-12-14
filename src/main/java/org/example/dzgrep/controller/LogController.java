package org.example.dzgrep.controller;

import org.example.dzgrep.service.LogService;
import org.example.dzgrep.vo.LogContextParam;
import org.example.dzgrep.vo.LogQueryParam;
import org.example.dzgrep.vo.LogView;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/log")
public class LogController {

  LogService logService;

  public LogController(LogService logService) {
    this.logService = logService;
  }

  @RequestMapping(value = "/query", method = RequestMethod.POST)
  public ResponseEntity<LogView> createLogQuery(@RequestBody LogQueryParam logQueryParam) {
    try {
      return ResponseEntity.ok(logService.createLogQuery(logQueryParam));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @RequestMapping(value = "/cancel", method = RequestMethod.GET)
  public ResponseEntity<Boolean> cancelLogQuery(@RequestParam String queryId) {
    logService.cancelLogQuery(queryId);
    return ResponseEntity.ok(true);
  }

  @RequestMapping(value = "/check", method = RequestMethod.GET)
  public ResponseEntity<LogView> getExistingLogQuery(@RequestParam String queryId) {
    return null;
  }

  @RequestMapping(value = "/context", method = RequestMethod.POST)
  public ResponseEntity<String> getLogContext(@RequestBody LogContextParam logContextParam) {
    try {
      return ResponseEntity.ok(logService.getLogContext(logContextParam));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @RequestMapping(value = "/next", method = RequestMethod.GET)
  public ResponseEntity<LogView> getNextPage(@RequestParam String queryId) {
    try {
      return ResponseEntity.ok(logService.getNextPage(queryId));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }
}

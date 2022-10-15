package org.example.dzgrep.controller;

import org.example.dzgrep.service.LogService;
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
    return null;
  }

  @RequestMapping(value = "/check", method = RequestMethod.GET)
  public ResponseEntity<LogView> getExistingLogQuery(@RequestParam String queryId) {
    return null;
  }
}

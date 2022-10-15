package org.example.dzgrep.controller;

import org.example.dzgrep.service.ServerService;
import org.example.dzgrep.vo.ServerView;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/server")
public class ServerController {

  private final ServerService serverService;

  public ServerController(ServerService serverService) {
    this.serverService = serverService;
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public ResponseEntity<Boolean> registerServer(@RequestBody ServerView serverView) {
    serverService.registerServer(serverView);
    return ResponseEntity.ok(true);
  }

  @RequestMapping(value = "/modify", method = RequestMethod.POST)
  public ResponseEntity<Boolean> modifyServer(@RequestBody ServerView serverView) {
    serverService.modifyServer(serverView);
    return ResponseEntity.ok(true);
  }

  @RequestMapping(value = "/remove", method = RequestMethod.GET)
  public ResponseEntity<Boolean> removeServer(@RequestParam("ip") String serverIp) {
    serverService.removeServer(serverIp);
    return ResponseEntity.ok(true);
  }

  @RequestMapping(value = "/activate", method = RequestMethod.GET)
  public ResponseEntity<Boolean> activateServer(@RequestParam("ip") String serverIp) {
    serverService.activateServer(serverIp);
    return ResponseEntity.ok(true);
  }

  @RequestMapping(value = "/deactivate", method = RequestMethod.GET)
  public ResponseEntity<Boolean> deactivateServer(@RequestParam("ip") String serverIp) {
    serverService.deactivateServer(serverIp);
    return ResponseEntity.ok(true);
  }
}

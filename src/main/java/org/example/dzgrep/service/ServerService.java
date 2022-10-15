package org.example.dzgrep.service;

import org.example.dzgrep.vo.ServerView;
import org.springframework.web.bind.annotation.RequestParam;

public interface ServerService {

  void registerServer(ServerView serverView);

  void modifyServer(ServerView serverView);

  void removeServer(String serverIp);

  void activateServer(String serverIp);

  void deactivateServer(@RequestParam String serverIp);
}

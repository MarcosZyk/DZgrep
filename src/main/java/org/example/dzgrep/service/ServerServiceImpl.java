package org.example.dzgrep.service;

import org.example.dzgrep.entity.ServerInfo;
import org.example.dzgrep.store.ServerStore;
import org.example.dzgrep.vo.ServerView;
import org.springframework.stereotype.Service;

@Service
public class ServerServiceImpl implements ServerService {

  private final ServerStore serverStore;

  public ServerServiceImpl(ServerStore serverStore) {
    this.serverStore = serverStore;
  }

  @Override
  public void registerServer(ServerView serverView) {
    serverStore.registerServer(getServerInfo(serverView));
  }

  @Override
  public void modifyServer(ServerView serverView) {
    serverStore.modifyServer(getServerInfo(serverView));
  }

  @Override
  public void removeServer(String serverIp) {
    serverStore.removeServer(serverIp);
  }

  @Override
  public void activateServer(String serverIp) {
    serverStore.activateServer(serverIp);
  }

  @Override
  public void deactivateServer(String serverIp) {
    serverStore.deactivateServer(serverIp);
  }

  private ServerInfo getServerInfo(ServerView serverView) {
    return new ServerInfo(
        serverView.getIp(),
        serverView.getUsername(),
        serverView.getPassword(),
        serverView.getLogDir(),
        serverView.isActive());
  }
}

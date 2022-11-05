package org.example.dzgrep.store;

import org.example.dzgrep.entity.ServerInfo;

import java.util.List;

public interface ServerStore {

  void registerServer(ServerInfo serverInfo);

  void modifyServer(ServerInfo serverInfo);

  void removeServer(String serverIp);

  void activateServer(String serverIp);

  void deactivateServer(String serverIp);

  ServerInfo getServer(String serverIp);

  List<ServerInfo> getServerList(List<String> serverIpList);
}

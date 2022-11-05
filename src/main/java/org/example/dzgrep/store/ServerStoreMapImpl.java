package org.example.dzgrep.store;

import org.example.dzgrep.entity.ServerInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ServerStoreMapImpl implements ServerStore {

  private final Map<String, ServerInfo> serverInfoMap = new ConcurrentHashMap<>();

  @Override
  public void registerServer(ServerInfo serverInfo) {
    serverInfoMap.put(serverInfo.getIp(), serverInfo);
  }

  @Override
  public void modifyServer(ServerInfo serverInfo) {
    serverInfoMap.put(serverInfo.getIp(), serverInfo);
  }

  @Override
  public void removeServer(String serverIp) {
    serverInfoMap.remove(serverIp);
  }

  @Override
  public void activateServer(String serverIp) {
    serverInfoMap.get(serverIp).setActive(true);
  }

  @Override
  public void deactivateServer(String serverIp) {
    serverInfoMap.get(serverIp).setActive(false);
  }

  @Override
  public ServerInfo getServer(String serverIp) {
    return serverInfoMap.get(serverIp);
  }

  @Override
  public List<ServerInfo> getServerList(List<String> serverIpList) {
    return serverIpList.stream().map(serverInfoMap::get).collect(Collectors.toList());
  }
}

package cn.shellming.thrift.client.loadbalancer;

import cn.shellming.thrift.client.common.ThriftServerNode;
import cn.shellming.thrift.client.discovery.ServerListUpdater;
import cn.shellming.thrift.client.discovery.ThriftConsulServerListUpdater;
import cn.shellming.thrift.client.discovery.ThriftConsulServerNode;
import cn.shellming.thrift.client.discovery.ThriftConsulServerNodeList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ThriftConsulServerListLoadBalancer extends AbstractLoadBalancer {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ThriftConsulServerNodeList serverNodeList;
    private IRule rule;

    private volatile ServerListUpdater serverListUpdater;

    private final ServerListUpdater.UpdateAction updateAction = this::updateListOfServers;

    public ThriftConsulServerListLoadBalancer(ThriftConsulServerNodeList serverNodeList, IRule rule) {
        this.serverNodeList = serverNodeList;
        this.rule = rule;
        this.serverListUpdater = new ThriftConsulServerListUpdater();
        this.startUpdateAction();
    }

    @Override
    public ThriftConsulServerNodeList getThriftServerNodeList() {
        return this.serverNodeList;
    }

    @Override
    public ThriftConsulServerNode chooseServerNode(String key) {
        if (rule == null) {
            return null;
        } else {
            ThriftServerNode serverNode;
            try {
                serverNode = rule.choose(key);
            } catch (Exception e) {
                log.warn("LoadBalancer [{}]:  Error choosing server for key {}", getClass().getSimpleName(), key, e);
                return null;
            }

            if (serverNode instanceof ThriftConsulServerNode) {
                return (ThriftConsulServerNode) serverNode;
            }
        }

        return null;
    }

    private synchronized void startUpdateAction() {
        log.info("Using serverListUpdater {}", serverListUpdater.getClass().getSimpleName());
        if (serverListUpdater == null) {
            serverListUpdater = new ThriftConsulServerListUpdater();
        }

        this.serverListUpdater.start(updateAction);
    }

    public void stopServerListRefreshing() {
        if (serverListUpdater != null) {
            serverListUpdater.stop();
        }
    }

    private void updateListOfServers() {
        Map<String, LinkedHashSet<ThriftConsulServerNode>> thriftConsulServers = this.serverNodeList.refreshThriftServers();

        List<String> serverList = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, LinkedHashSet<ThriftConsulServerNode>> serverEntry : thriftConsulServers.entrySet()) {
            serverList.add(
                    sb.append(serverEntry.getKey())
                            .append(": ")
                            .append(serverEntry.getValue())
                            .toString()
            );
            sb.setLength(0);
        }

        log.info("Refreshed thrift serverList: [" + String.join(", ", serverList) + "]");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ThriftConsulServerListLoadBalancer:");
        sb.append(super.toString());
        sb.append("ServerList:").append(String.valueOf(serverNodeList));
        return sb.toString();
    }
}

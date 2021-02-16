package cn.shellming.thrift.client.loadbalancer;


import cn.shellming.thrift.client.common.ThriftServerNode;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public interface ILoadBalancer<T extends ThriftServerNode> {

    T chooseServerNode(String key);

    Map<String, LinkedHashSet<T>> getAllServerNodes();

    Map<String, LinkedHashSet<T>> getRefreshedServerNodes();

    List<T> getServerNodes(String key);

    List<T> getRefreshedServerNodes(String key);

}

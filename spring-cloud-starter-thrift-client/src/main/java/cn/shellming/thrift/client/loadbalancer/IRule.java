package cn.shellming.thrift.client.loadbalancer;


import cn.shellming.thrift.client.common.ThriftServerNode;

public interface IRule {

    ThriftServerNode choose(String key);

    void setLoadBalancer(ILoadBalancer lb);

    ILoadBalancer getLoadBalancer();

}

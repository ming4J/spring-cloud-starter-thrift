package cn.shellming.thrift.server.context;

import cn.shellming.thrift.server.properties.ThriftServerProperties;
import cn.shellming.thrift.server.wrapper.ThriftServiceWrapper;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.List;

public interface ContextBuilder {

    ContextBuilder prepare();

    TServer buildThriftServer(ThriftServerProperties properties,
                              List<ThriftServiceWrapper> serviceWrappers)
            throws TTransportException, IOException;

}

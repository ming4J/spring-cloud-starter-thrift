package cn.shellming.thrift.server.context;

import cn.shellming.thrift.server.argument.TSimpleServerArgument;
import cn.shellming.thrift.server.properties.ThriftServerProperties;
import cn.shellming.thrift.server.wrapper.ThriftServiceWrapper;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TSimpleServerContext implements ContextBuilder {

    private static TSimpleServerContext serverContext;
    private TSimpleServer.Args args;

    private TSimpleServerContext() {
    }

    public static TSimpleServerContext context() {
        if (Objects.isNull(serverContext)) {
            serverContext = new TSimpleServerContext();
        }
        return serverContext;
    }

    @Override
    public ContextBuilder prepare() {
        return context();
    }

    @Override
    public TServer buildThriftServer(ThriftServerProperties properties,
                                     List<ThriftServiceWrapper> serviceWrappers)
            throws TTransportException, IOException {
        serverContext = (TSimpleServerContext) prepare();
        serverContext.args = new TSimpleServerArgument(serviceWrappers, properties);
        return new TSimpleServer(serverContext.args);
    }

}

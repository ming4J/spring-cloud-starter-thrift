package cn.shellming.thrift.server.argument;

import cn.shellming.thrift.server.exception.ThriftServerException;
import cn.shellming.thrift.server.processor.TRegisterProcessor;
import cn.shellming.thrift.server.processor.TRegisterProcessorFactory;
import cn.shellming.thrift.server.properties.TThreadPoolServerProperties;
import cn.shellming.thrift.server.properties.ThriftServerProperties;
import cn.shellming.thrift.server.wrapper.ThriftServiceWrapper;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TThreadPoolServerArgument extends TThreadPoolServer.Args {

    private Map<String, ThriftServiceWrapper> processorMap = new HashMap<>();

    public TThreadPoolServerArgument(List<ThriftServiceWrapper> serviceWrappers, ThriftServerProperties properties)
            throws TTransportException, IOException {
        super(new TServerSocket(new ServerSocket(properties.getPort())));

        transportFactory(new TFastFramedTransport.Factory());
        protocolFactory(new TCompactProtocol.Factory());

        TThreadPoolServerProperties threadPoolProperties = properties.getThreadPool();

        minWorkerThreads(threadPoolProperties.getMinWorkerThreads());
        maxWorkerThreads(threadPoolProperties.getMaxWorkerThreads());
        requestTimeout(threadPoolProperties.getRequestTimeout());

        executorService(createInvokerPool(properties));

        try {
            TRegisterProcessor registerProcessor = TRegisterProcessorFactory.registerProcessor(serviceWrappers);

            processorMap.clear();
            processorMap.putAll(registerProcessor.getProcessorMap());

            processor(registerProcessor);
        } catch (Exception e) {
            throw new ThriftServerException("Can not create multiplexed processor for " + serviceWrappers, e);
        }
    }

    private ExecutorService createInvokerPool(ThriftServerProperties properties) {
        TThreadPoolServerProperties threadPoolProperties = properties.getThreadPool();

        return new ThreadPoolExecutor(
                threadPoolProperties.getMinWorkerThreads(),
                threadPoolProperties.getMaxWorkerThreads(),
                threadPoolProperties.getKeepAlivedTime(), TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(properties.getWorkerQueueCapacity()));
    }

    public Map<String, ThriftServiceWrapper> getProcessorMap() {
        return processorMap;
    }

}

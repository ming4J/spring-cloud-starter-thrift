package cn.shellming.thrift.server.exception;

public class ThriftServerInstantiateException extends ThriftServerException {

    public ThriftServerInstantiateException(String message) {
        super(message);
    }

    public ThriftServerInstantiateException(String message, Throwable t) {
        super(message, t);
    }
}

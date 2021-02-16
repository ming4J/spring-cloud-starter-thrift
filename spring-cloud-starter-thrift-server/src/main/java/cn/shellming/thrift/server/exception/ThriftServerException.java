package cn.shellming.thrift.server.exception;


public class ThriftServerException extends RuntimeException {

    public ThriftServerException(String message) {
        super(message);
    }

    public ThriftServerException(String message, Throwable t) {
        super(message, t);
    }
}

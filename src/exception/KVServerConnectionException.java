package exception;

import server.KVServer;

public class KVServerConnectionException extends RuntimeException {

    public KVServerConnectionException(String errorMessage) {
        super(errorMessage);
    }

    public KVServerConnectionException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

    public KVServerConnectionException(Throwable cause) {
        super(cause);
    }
}

package exception;

public class TaskFormatException extends RuntimeException {

    public TaskFormatException(String message) {
        super(message);
    }

    public TaskFormatException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

    public TaskFormatException(Throwable cause) {
        super(cause);
    }
}

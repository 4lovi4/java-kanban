package exception;

public class TaskFormatException extends RuntimeException {
    public TaskFormatException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}


package exception;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

    public ManagerSaveException(Throwable cause) {
        super(cause);
    }
}

package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
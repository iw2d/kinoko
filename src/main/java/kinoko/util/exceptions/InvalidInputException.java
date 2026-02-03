package kinoko.util.exceptions;

/**
 * Thrown when the input is invalid,
 * for example a target user does not exist.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}
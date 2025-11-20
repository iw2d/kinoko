package kinoko.util.exceptions;

/**
 * Thrown when a situation occurs that is clearly the result of a developer's mistake
 * rather than user input or runtime conditions. Typically indicates that an internal
 * assumption, invariant, or system design contract has been violated.
 *
 * This exception is unchecked (extends RuntimeException) because it represents
 * a logic or design error that should be fixed in code, not handled at runtime.
 */
public class DumbDeveloperFound extends RuntimeException {
    public DumbDeveloperFound(String message) {
        super(message);
    }
}
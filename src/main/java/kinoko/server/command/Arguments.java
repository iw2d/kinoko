package kinoko.server.command;

import java.lang.annotation.*;

/**
 * Annotation for command arguments.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {
    /**
     * Returns an array of arguments that are required to invoke the command.
     *
     * @return an array of arguments that are required to invoke the command.
     */
    String[] value();
}

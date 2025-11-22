package kinoko.server.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

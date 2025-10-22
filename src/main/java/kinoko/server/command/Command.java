package kinoko.server.command;


import java.lang.annotation.*;

/**
 * Annotation for in-game commands. The annotated command methods will be registered at runtime using reflection.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Returns an array of the command aliases that can be used to invoke the command.
     *
     * @return an array of the command aliases that can be used to invoke the command.
     */
    String[] value();
}

package kinoko.handler;

import kinoko.server.header.InHeader;

import java.lang.annotation.*;

/**
 * Indicates that the annotated method is a handler method for the specified {@link InHeader} operation codes. The
 * annotated handler methods will be registered at runtime using reflection.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Handler {
    /**
     * Returns an array of the {@link InHeader} operation codes that should be handled by the method.
     *
     * @return an array of the {@link InHeader} operation codes that should be handled by the method.
     */
    InHeader[] value();
}

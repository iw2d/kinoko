package kinoko.script.common;

import java.lang.annotation.*;

/**
 * Annotation for game scripts. The annotated script methods will be registered at runtime using reflection.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Script {
    /**
     * Returns an array of the script aliases that can be used to reference the script.
     *
     * @return an array of the script aliases that can be used to reference the script.
     */
    String value();
}

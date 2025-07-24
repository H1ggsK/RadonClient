package skid.krypton.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreInit {
    Priority priority() default Priority.HIGHEST;
    Class<?>[] dependencies() default { };
}
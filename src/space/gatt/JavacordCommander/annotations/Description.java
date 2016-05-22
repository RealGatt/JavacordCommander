package space.gatt.JavacordCommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description Annotation
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.

// Command description

public @interface Description {
	String value();
}

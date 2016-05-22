package space.gatt.JavacordCommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command Annotation
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.
public @interface Command {
	String value();
}

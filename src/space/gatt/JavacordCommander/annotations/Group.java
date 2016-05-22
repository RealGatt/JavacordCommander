package space.gatt.JavacordCommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Group Annotation
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.

// The command group, for later use so I can easily generate the help command

public @interface Group {
	String value();
}

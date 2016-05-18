package space.gatt.JavacordCommander.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Zach G on 06-Apr-16.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in method only.
public @interface CommandSettings {
	String[] value() default "";
	boolean deleteInitatingMsg() default false;
	boolean sendResponseViaPM() default false;
	boolean requiresPM() default false;
	Class returnType() default String.class;
}

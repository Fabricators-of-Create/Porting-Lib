package io.github.fabricators_of_create.porting_lib.gametest.infrastructure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows for test method declarations to be concise by moving subdirectories and namespaces to the class level.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameTestGroup {
	/**
	 * The namespace to search for test structures in.
	 */
	String namespace();

	/**
	 * The subdirectory to search for test structures in.
	 */
	String path() default "";
}

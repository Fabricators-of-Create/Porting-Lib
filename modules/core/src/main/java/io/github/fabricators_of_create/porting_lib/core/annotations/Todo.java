package io.github.fabricators_of_create.porting_lib.core.annotations;

// Only use with classes, and methods that aren't implemented yet, this should only be used for when porting to newer versions
public @interface Todo {
	boolean notImplemented() default false;

	String rename() default "";
}

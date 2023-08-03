package io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.fabricators_of_create.porting_lib.mixin_extensions.points.WrappableInjectionPoint;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Custom mixin annotation, can be used to wrap a variable use in a method
 * can currently target local access, array lookups, and casts
 * should be used with {@link WrappableInjectionPoint}
 * CHECK EXPORTS, THIS CAN BE FRAGILE
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapVariable {
	String[] method();

	At[] at();

	Slice[] slice() default {};

	boolean remap() default true;

	int require() default -1;

	int expect() default 1;

	int allow() default -1;
}

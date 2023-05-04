package io.github.fabricators_of_create.porting_lib.gametest.infrastructure;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;

/**
 * Allows test methods or entire classes to use a custom subclass of {@link GameTestHelper}.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomGameTestHelper {
	/**
	 * The class of the custom {@link GameTestHelper} subclass.
	 * This subclass must have a constructor taking one parameter: {@link GameTestInfo}.
	 */
	Class<? extends GameTestHelper> value();
}

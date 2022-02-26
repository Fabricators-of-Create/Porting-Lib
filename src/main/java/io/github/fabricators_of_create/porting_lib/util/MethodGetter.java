package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.PortingLib;

import java.lang.reflect.Method;

public class MethodGetter {
	public static Method findMethod(Class<?> clas, String methodName, String intermediaryName, Class<?>... parameterTypes) {
		Method method;
		try {
			// obfuscated
			method = clas.getMethod(intermediaryName, parameterTypes);

		} catch (NoSuchMethodException e) {
			// un-obfuscated
			try {
				method = clas.getMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException ex) {
				PortingLib.LOGGER.error("No method with the provided name or obfuscated name found!");
				throw new RuntimeException(ex);
			}
		}
		return method;
	}
}

package io.github.fabricators_of_create.porting_lib.mixin_extensions.init;

import io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable.WrapVariableInjectionInfo;
import io.github.fabricators_of_create.porting_lib.mixin_extensions.points.WrappableInjectionPoint;

import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;

public class PortingLibMixinExtensions {
	public static void init() {
		InjectionInfo.register(WrapVariableInjectionInfo.class);
		InjectionPoint.register(WrappableInjectionPoint.class, "PORTING_LIB");
	}
}

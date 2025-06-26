package io.github.fabricators_of_create.porting_lib.common.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public interface LanguageManagerInjection {
	default Locale port_lib$getJavaLocale() {
		throw PortingLib.createMixinException(this.getClass().getSimpleName() + " does not support getJavaLocale()");
	}
}

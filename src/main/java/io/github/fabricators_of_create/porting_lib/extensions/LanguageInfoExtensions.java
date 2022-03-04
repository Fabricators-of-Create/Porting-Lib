package io.github.fabricators_of_create.porting_lib.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public interface LanguageInfoExtensions {
	default Locale getJavaLocale() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}

package io.github.fabricators_of_create.porting_lib.common.mixin.client;

import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.common.extensions.LanguageInfoExtensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.LanguageInfo;

@Environment(EnvType.CLIENT)
@Mixin(LanguageInfo.class)
public abstract class LanguageInfoMixin implements LanguageInfoExtensions {
	@Shadow
	@Final
	private String code;
	@Unique
	private Locale port_lib$javaLocale;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void port_lib$addLocale(String string, String string2, String string3, boolean bl, CallbackInfo ci) {
		String[] splitLangCode = code.split("_", 2);
		if (splitLangCode.length == 1) { // Vanilla has some languages without underscores
			this.port_lib$javaLocale = new Locale(code);
		} else {
			this.port_lib$javaLocale = new Locale(splitLangCode[0], splitLangCode[1]);
		}
	}

	@Override
	public Locale getJavaLocale() {
		return port_lib$javaLocale;
	}
}

package io.github.fabricators_of_create.porting_lib.common.mixin.client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.client.resources.language.LanguageManager;

import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.common.extensions.LanguageManagerExtensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.LanguageInfo;

@Environment(EnvType.CLIENT)
@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin implements LanguageManagerExtensions {
	@Shadow
	private Map<String, LanguageInfo> languages;

	@Shadow
	public abstract String getSelected();

	@Unique
	private final Map<String, Locale> codeToLocales = new HashMap<>();

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"))
	private void updateLocales(ResourceManager resourceManager, CallbackInfo ci) {
		codeToLocales.clear();
		this.languages.forEach((s, languageInfo) -> {
			String[] splitLangCode = s.split("_", 2);
			if (splitLangCode.length == 1) { // Vanilla has some languages without underscores
				codeToLocales.put(s, new Locale(s));
			} else {
				codeToLocales.put(s, new Locale(splitLangCode[0], splitLangCode[1]));
			}
		});
	}

	@Override
	public Locale getJavaLocale(String code) {
		return codeToLocales.get(code);
	}

	@Override
	public Locale getSelectedJavaLocale() {
		return getJavaLocale(getSelected());
	}
}

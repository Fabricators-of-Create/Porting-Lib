package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.util.ForgeI18n;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.I18n;

import net.minecraft.locale.Language;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(I18n.class)
public abstract class I18nMixin {
	@Inject(method = "setLanguage", at = @At("TAIL"))
	private static void port_lib$setLanguage(Language language, CallbackInfo ci) {
		if (language instanceof ClientLanguage clientLanguage)
			ForgeI18n.loadLanguageData(((ClientLanguageAccessor)clientLanguage).port_lib$getStorage());
		else
			ForgeI18n.loadLanguageData(ImmutableMap.of());
	}
}

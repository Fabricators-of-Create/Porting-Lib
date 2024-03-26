package io.github.fabricators_of_create.porting_lib.common.mixin.client;

import java.util.Locale;

import net.minecraft.client.resources.language.LanguageManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.common.extensions.LanguageManagerExtensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LanguageManager.class)
public abstract class LanguageManagerMixin implements LanguageManagerExtensions {
	@Shadow
	public abstract String getSelected();

	@Unique
	private Locale javaLocale;

	@Inject(method = { "setSelected", "<init>" }, at = @At("TAIL"))
	private void updateLocale(CallbackInfo ci) {
		String[] split = this.getSelected().split("_", 2);
		this.javaLocale = split.length == 1 ? new Locale(split[0]) : new Locale(split[0], split[1]);
	}

	@Override
	public Locale getJavaLocale(String code) {
		return Locale.ROOT;
	}

	@Override
	public Locale getSelectedJavaLocale() {
		return this.javaLocale;
	}
}

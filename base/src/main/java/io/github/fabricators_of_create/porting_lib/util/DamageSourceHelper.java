package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.DamageSourceAccessor;

import net.minecraft.world.damagesource.DamageSource;

public final class DamageSourceHelper {
	public static DamageSource port_lib$createDamageSource(String string) {
		return DamageSourceAccessor.port_lib$init(string);
	}

	// this is probably going to crash and burn.
	public static DamageSource port_lib$createArmorBypassingDamageSource(String string) {
		return MixinHelper.<DamageSourceAccessor>cast(port_lib$createDamageSource(string)).port_lib$setDamageBypassesArmor();
	}

	public static DamageSource port_lib$createFireDamageSource(String string) {
		return MixinHelper.<DamageSourceAccessor>cast(port_lib$createDamageSource(string)).port_lib$setFireDamage();
	}

	private DamageSourceHelper() {}
}

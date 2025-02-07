package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface DamageSourceAccessor {
	@Invoker("<init>")
	static DamageSource port_lib$init(String string) {
		throw new AssertionError();
	}

	@Invoker("setIsFire")
	DamageSource port_lib$setFireDamage();

	@Invoker("bypassArmor")
	DamageSource port_lib$setDamageBypassesArmor();

	@Invoker("bypassInvul")
	DamageSource port_lib$bypassInvul();

	@Invoker("bypassMagic")
	DamageSource port_lib$bypassMagic();

	@Invoker("damageHelmet")
	DamageSource port_lib$damageHelmet();
}

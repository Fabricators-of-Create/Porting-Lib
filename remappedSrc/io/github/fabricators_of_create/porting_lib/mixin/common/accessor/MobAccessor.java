package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEntity.class)
public interface MobAccessor {
	@Invoker("maybeDisableShield")
	void port_lib$maybeDisableShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack);
}

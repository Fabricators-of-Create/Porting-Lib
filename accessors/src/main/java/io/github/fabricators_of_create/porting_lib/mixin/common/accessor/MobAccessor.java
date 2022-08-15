package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mob.class)
public interface MobAccessor {
	@Invoker("maybeDisableShield")
	void port_lib$maybeDisableShield(Player player, ItemStack mobStack, ItemStack playerStack);
}

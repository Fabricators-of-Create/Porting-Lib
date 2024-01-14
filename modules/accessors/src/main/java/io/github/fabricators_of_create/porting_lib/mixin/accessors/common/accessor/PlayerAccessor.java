package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public interface PlayerAccessor {
	@Invoker("closeContainer")
	void port_lib$closeScreen();
}

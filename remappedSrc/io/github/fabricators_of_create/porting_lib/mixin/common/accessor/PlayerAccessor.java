package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerEntity.class)
public interface PlayerAccessor {
	@Invoker("closeContainer")
	void port_lib$closeScreen();
}

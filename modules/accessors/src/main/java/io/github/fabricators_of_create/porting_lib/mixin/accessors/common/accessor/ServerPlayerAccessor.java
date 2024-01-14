package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerAccessor {
	@Invoker
	void callInitMenu(AbstractContainerMenu abstractContainerMenu);

	@Invoker
	void callNextContainerCounter();

	@Accessor
	int getContainerCounter();
}

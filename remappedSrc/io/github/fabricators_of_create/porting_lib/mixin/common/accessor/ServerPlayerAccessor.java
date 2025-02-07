package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerAccessor {
	@Invoker
	void callInitMenu(ScreenHandler abstractContainerMenu);

	@Invoker
	void callNextContainerCounter();

	@Accessor
	int getContainerCounter();
}

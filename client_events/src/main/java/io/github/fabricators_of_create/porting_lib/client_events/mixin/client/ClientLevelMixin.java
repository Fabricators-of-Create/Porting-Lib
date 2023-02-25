package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.RegisterColorResolversCallback;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ColorResolver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	@Shadow
	public abstract int calculateBlockTint(BlockPos blockPos, ColorResolver colorResolver);

	@Inject(method = "method_23778", at = @At("TAIL"))
	private void addCustomColorResolvers(Object2ObjectArrayMap<ColorResolver, BlockTintCache> target, CallbackInfo ci) {
		RegisterColorResolversCallback.RegistrationHelper helper = new RegisterColorResolversCallback.RegistrationHelper();
		RegisterColorResolversCallback.EVENT.invoker().onColorResolverCreation(helper);
		for (var resolver : helper.build()) {
			target.put(resolver, new BlockTintCache(pos -> calculateBlockTint(pos, resolver)));
		}
	}
}

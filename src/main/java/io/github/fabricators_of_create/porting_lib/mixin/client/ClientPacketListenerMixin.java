package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;

import net.minecraft.world.item.crafting.RecipeManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	@Final
	private Connection connection;

	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Inject(method = "method_38542", at = @At("HEAD"), cancellable = true)
	public void port_lib$handleCustomBlockEntity(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity, CallbackInfo ci) {
		if (blockEntity instanceof CustomDataPacketHandlingBlockEntity handler) {
			handler.onDataPacket(connection, packet);
			ci.cancel();
		}
	}

	@Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
	public void port_lib$updateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
		RecipesUpdatedCallback.EVENT.invoker().onRecipesUpdated(this.recipeManager);
	}
}

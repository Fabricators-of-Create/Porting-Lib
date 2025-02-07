package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import io.github.fabricators_of_create.porting_lib.event.common.TagsUpdatedCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.registry.DynamicRegistryManager;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	@Final
	private ClientConnection connection;

	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Shadow
	private DynamicRegistryManager.Frozen registryAccess;

	@Inject(method = "method_38542", at = @At("HEAD"), cancellable = true)
	public void port_lib$handleCustomBlockEntity(BlockEntityUpdateS2CPacket packet, BlockEntity blockEntity, CallbackInfo ci) {
		if (blockEntity instanceof CustomDataPacketHandlingBlockEntity handler) {
			handler.onDataPacket(connection, packet);
			ci.cancel();
		}
	}

	@Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
	public void port_lib$updateRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo ci) {
		RecipesUpdatedCallback.EVENT.invoker().onRecipesUpdated(this.recipeManager);
	}

	@Inject(method = "handleUpdateTags", at = @At("TAIL"))
	public void port_lib$updateTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
		TagsUpdatedCallback.EVENT.invoker().onTagsUpdated(this.registryAccess);
	}
}

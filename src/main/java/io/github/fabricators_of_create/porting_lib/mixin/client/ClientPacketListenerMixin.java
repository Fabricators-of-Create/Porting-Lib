package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeManager;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.extensions.ClientboundAddEntityPacketExtensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	@Final
	private Connection connection;

	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Shadow
	public abstract Set<ResourceKey<Level>> levels();

	@Inject(
			method = "handleAddEntity(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/Entity;recreateFromPacket(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V",
					shift = Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$afterRecreateEntity(ClientboundAddEntityPacket packet, CallbackInfo ci, EntityType<?> entityType, Entity entity) {
		if (entity instanceof ExtraSpawnDataEntity extra) {
			FriendlyByteBuf extraData = ((ClientboundAddEntityPacketExtensions)packet).getExtraDataBuf();
			if (extraData != null) {
				extra.readSpawnData(extraData);
				extraData.release();
			}
		}
	}
//
//	@Redirect(method = "handleAddEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"))
//	public Entity clientEntity(EntityType instance, Level level) {
//		return instance.create(level);
//	}

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

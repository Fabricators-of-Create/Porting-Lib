package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import io.github.fabricators_of_create.porting_lib.event.common.RecipesUpdatedCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
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
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Final
	@Shadow
	private RegistryAccess.Frozen registryAccess;

	protected ClientPacketListenerMixin(Minecraft client, Connection connection, CommonListenerCookie commonListenerCookie) {
		super(client, connection, commonListenerCookie);
	}

	@Inject(method = "method_38542", at = @At("HEAD"), cancellable = true)
	public void andleCustomBlockEntity(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity, CallbackInfo ci, @Share("data_packet") LocalBooleanRef handleRef) {
		if (blockEntity instanceof CustomDataPacketHandlingBlockEntity handler) {
			handler.onDataPacket(connection, packet, this.registryAccess);
			handleRef.set(true);
		} else
			handleRef.set(false);
	}

	@WrapOperation(method = "method_38542", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;isEmpty()Z"))
	private boolean checkIfUpdateAlreadyHandled(CompoundTag instance, Operation<Boolean> original, @Share("data_packet") LocalBooleanRef handleRef) {
		if (!handleRef.get())
			return original.call(instance);
		return true;
	}

	@Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
	public void port_lib$updateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
		RecipesUpdatedCallback.EVENT.invoker().onRecipesUpdated(this.recipeManager);
	}
}

package io.github.fabricators_of_create.porting_lib.resources.mixin.client;

import io.github.fabricators_of_create.porting_lib.resources.events.RecipesUpdatedEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;

import net.minecraft.world.item.crafting.RecipeManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
	@Shadow
	@Final
	private RecipeManager recipeManager;

	@Inject(method = "handleUpdateRecipes", at = @At("TAIL"))
	public void port_lib$updateRecipes(ClientboundUpdateRecipesPacket packet, CallbackInfo ci) {
		new RecipesUpdatedEvent(this.recipeManager).sendEvent();
	}
}

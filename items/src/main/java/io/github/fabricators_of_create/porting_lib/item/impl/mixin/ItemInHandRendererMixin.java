package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.ReequipAnimationItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
	@Shadow
	private ItemStack mainHandItem;
	@Shadow
	private ItemStack offHandItem;

	@Unique
	private static int port_lib$mainHandSlot = 0;

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$tick(CallbackInfo ci,
							  LocalPlayer clientPlayerEntity, ItemStack itemStack, ItemStack itemStack2) {
		if (!port_lib$shouldCauseReequipAnimation(mainHandItem, itemStack, clientPlayerEntity.getInventory().selected)) {
			mainHandItem = itemStack;
		}

		if (!port_lib$shouldCauseReequipAnimation(offHandItem, itemStack2, -1)) {
			offHandItem = itemStack2;
		}
	}

	@Unique
	private static boolean port_lib$shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
		if (!from.isEmpty() && !to.isEmpty()) {
			boolean changed = false;
			if (slot != -1) {
				changed = slot != port_lib$mainHandSlot;
				port_lib$mainHandSlot = slot;
			}

			if (from.getItem() instanceof ReequipAnimationItem handler) {
				return handler.shouldCauseReequipAnimation(from, to, changed);
			}
		}
		return true;
	}
}

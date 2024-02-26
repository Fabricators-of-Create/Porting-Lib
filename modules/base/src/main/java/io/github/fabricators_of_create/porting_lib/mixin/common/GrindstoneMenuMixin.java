package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents;
import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents.OnTakeItem;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.GrindstoneMenuExtension;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu implements GrindstoneMenuExtension {
	@Shadow
	@Final
	public Container repairSlots;

	@Shadow
	@Final
	private Container resultSlots;

	protected GrindstoneMenuMixin(@Nullable MenuType<?> menuType, int i) {
		super(menuType, i);
	}

	@Inject(
			method = "createResult",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
					ordinal = 3,
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	private void handleResult(CallbackInfo ci) {
		ItemStack top = this.repairSlots.getItem(0);
		ItemStack bottom = this.repairSlots.getItem(1);
		this.xp = PortingHooks.onGrindstoneChange(top, bottom, this.resultSlots, -1);
		if (this.xp != Integer.MIN_VALUE) {
			ci.cancel();
			broadcastChanges();
		}
	}

	@Unique
	private int xp = -1;

	@Override
	public int getXp() {
		return this.xp;
	}

	@Mixin(targets = "net/minecraft/world/inventory/GrindstoneMenu$4")
	public abstract static class GrindstoneMenuOutputSlotMixin {
		@Shadow(aliases = "field_16780")
		@Final
		GrindstoneMenu menu;

		@Unique
		private OnTakeItem takeEvent;

		// execute lambda
		@WrapOperation(
				method = "method_17417",
				at = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
				)
		)
		private void onGrindStoneTake(ServerLevel level, Vec3 pos, int amount, Operation<Void> original) {
			Container input = this.menu.repairSlots;
			ItemStack top = input.getItem(0);
			ItemStack bottom = input.getItem(1);
			this.takeEvent = new GrindstoneEvents.OnTakeItem(top, bottom, amount);
			takeEvent.sendEvent();
			if (!takeEvent.isCanceled())
				original.call(level, pos, takeEvent.getXp());
		}

		@ModifyArg(
				method = "onTake",
				at = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V",
						ordinal = 0
				),
				index = 1
		)
		private ItemStack modifyTopSlotResult(ItemStack original) {
			if (takeEvent != null) {
				ItemStack topItem = takeEvent.getNewTopItem();
				if (!topItem.isEmpty()) {
					return topItem;
				}
			}
			return original;
		}

		@ModifyArg(
				method = "onTake",
				at = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V",
						ordinal = 1
				),
				index = 1
		)
		private ItemStack modifyBottomSlotResult(ItemStack original) {
			if (takeEvent != null) {
				ItemStack bottomItem = takeEvent.getNewBottomItem();
				if (!bottomItem.isEmpty()) {
					return bottomItem;
				}
			}
			return original;
		}

		@WrapWithCondition(
				method = "onTake",
				at = @At(
						value = "INVOKE",
						target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"
				)
		)
		private boolean cancelInputRemoval(Container container, int slot, ItemStack stack) {
			return takeEvent == null || !takeEvent.isCanceled();
		}

		// forge does this, vanilla does not. Leaving just in case.
		@Inject(method = "onTake", at = @At("TAIL"))
		private void setChanged(Player player, ItemStack itemStack, CallbackInfo ci) {
			this.menu.repairSlots.setChanged();
		}

		@Inject(method = "getExperienceAmount", at = @At("HEAD"), cancellable = true)
		private void customXp(Level level, CallbackInfoReturnable<Integer> cir) {
			int exp = ((GrindstoneMenuExtension) this.menu).getXp();
			if (exp > -1)
				cir.setReturnValue(exp);
		}
	}
}

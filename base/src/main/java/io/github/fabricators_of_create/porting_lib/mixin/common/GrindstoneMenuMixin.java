package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.event.common.GrindstoneEvents;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.GrindstoneMenuExtension;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

	@Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 3, shift = At.Shift.AFTER), cancellable = true)
	private void handleResult(CallbackInfo ci) {
		ItemStack itemstack = this.repairSlots.getItem(0);
		ItemStack itemstack1 = this.repairSlots.getItem(1);
		this.xp = PortingHooks.onGrindstoneChange(itemstack, itemstack1, this.resultSlots, -1);
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
	public abstract static class GrindstoneMenu$4Mixin {
		@Shadow
		@Final
		GrindstoneMenu field_16780;

		@Shadow
		protected abstract int getExperienceAmount(Level par1);

		private ThreadLocal<GrindstoneEvents.OnTakeItem> EVENT = new ThreadLocal<>();

		@Inject(method = "method_17417", at = @At("HEAD"), cancellable = true)
		private void onGrindStoneTake(Level level, BlockPos blockPos, CallbackInfo ci) {
			var inputSlots = this.field_16780.repairSlots;
			GrindstoneEvents.OnTakeItem takeItem = new GrindstoneEvents.OnTakeItem(inputSlots.getItem(0), inputSlots.getItem(1), this.getExperienceAmount(level));
			takeItem.sendEvent();
			if (takeItem.isCanceled()) {
				ci.cancel();
				return;
			}
			EVENT.set(takeItem);
		}

		// Redirect breaks here cause of hidden classes
		@ModifyExpressionValue(method = "method_17417", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu$4;getExperienceAmount(Lnet/minecraft/world/level/Level;)I"))
		private int useEventXp(int oldLevel) {
			int newXp = EVENT.get().getXp();
			if (oldLevel == newXp)
				return oldLevel;
			return newXp;
		}

		@ModifyArg(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0), index = 1)
		private ItemStack modifyTop(ItemStack empty) {
			ItemStack topItem = EVENT.get().getNewTopItem();
			if (!topItem.isEmpty())
				return topItem;
			return empty;
		}

		@ModifyArg(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1), index = 1)
		private ItemStack modifyBottom(ItemStack empty) {
			ItemStack bottomItem = EVENT.get().getBottomItem();
			if (!bottomItem.isEmpty())
				return bottomItem;
			return empty;
		}

		@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1, shift = At.Shift.AFTER))
		private void setChanged(Player player, ItemStack itemStack, CallbackInfo ci) {
			this.field_16780.repairSlots.setChanged();
		}

		@WrapWithCondition(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
		private boolean shouldCancel(Container container, int amount, ItemStack stack) {
			return !EVENT.get().isCanceled();
		}

		@WrapWithCondition(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1))
		private boolean shouldCancel2(Container container, int amount, ItemStack stack) {
			boolean cancel = !EVENT.get().isCanceled();
			EVENT.remove();
			return cancel;
		}

		@Inject(method = "getExperienceAmount", at = @At("HEAD"), cancellable = true)
		private void customXp(Level level, CallbackInfoReturnable<Integer> cir) {
			if (((GrindstoneMenuExtension)this.field_16780).getXp() > -1) cir.setReturnValue(((GrindstoneMenuExtension)this.field_16780).getXp());
		}
	}
}

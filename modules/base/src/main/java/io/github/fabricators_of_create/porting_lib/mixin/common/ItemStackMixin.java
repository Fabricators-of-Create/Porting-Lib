package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.core.util.MutableDataComponentHolder;
import io.github.fabricators_of_create.porting_lib.entity.extensions.IShearable;
import io.github.fabricators_of_create.porting_lib.entity.extensions.VanillaIShearable;
import io.github.fabricators_of_create.porting_lib.item.DamageableItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;

import java.util.List;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = MutableDataComponentHolder.class, prefix = "port_lib$i$", remap = Interface.Remap.NONE))
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract void applyComponents(DataComponentPatch components);

	@Shadow
	public abstract void applyComponents(DataComponentMap components);

	@Shadow
	@Nullable
	public abstract <T> T set(DataComponentType<? super T> component, @Nullable T value);

	@Shadow
	@Nullable
	public abstract <T> T remove(DataComponentType<? extends T> component);

	@Inject(method = "setDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemSetDamage(int damage, CallbackInfo ci) {
		if(getItem() instanceof DamageableItem damagableItem) {
			damagableItem.setDamage((ItemStack) (Object) this, damage);
			ci.cancel();
		}
	}

	@Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemMaxDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getMaxDamage((ItemStack) (Object) this));
		}
	}

	@Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
	public void port_lib$itemDamage(CallbackInfoReturnable<Integer> cir) {
		if(getItem() instanceof DamageableItem damagableItem) {
			cir.setReturnValue(damagableItem.getDamage((ItemStack) (Object) this));
		}
	}

	@Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
	private void checkCustomShearBehavior(Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (entity instanceof IShearable target && !(entity instanceof VanillaIShearable)) {
			ItemStack stack = (ItemStack) (Object) this;
			BlockPos pos = entity.blockPosition();
			boolean isClient = entity.level().isClientSide();
			// Check isShearable on both sides (mirrors vanilla readyForShearing())
			if (target.isShearable(player, stack, entity.level(), pos)) {
				// Call onSheared on both sides (mirrors vanilla shear())
				List<ItemStack> drops = target.onSheared(player, stack, entity.level(), pos);
				// Spawn drops on the server side using spawnShearedDrop to retain vanilla mob-specific behavior
				if (!isClient) {
					for(ItemStack drop : drops) {
						target.spawnShearedDrop(entity.level(), pos, drop);
					}
				}
				// Call GameEvent.SHEAR on both sides
				entity.gameEvent(GameEvent.SHEAR, player);
				// Damage the shear item stack by 1 on the server side
				if (!isClient) {
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
				}
				// Return sided success if the entity was shearable
				cir.setReturnValue(InteractionResult.sidedSuccess(isClient));
			}
		}
	}

	@Intrinsic(displace = true)
	@Nullable
	public <T> T port_lib$i$set(DataComponentType<? super T> componentType, @Nullable T value) {
		return this.set(componentType, value);
	}

	@Intrinsic(displace = true)
	public <T> T port_lib$i$remove(DataComponentType<? extends T> componentType) {
		return this.remove(componentType);
	}

	@Intrinsic(displace = true)
	public void port_lib$i$applyComponents(DataComponentPatch patch) {
		this.applyComponents(patch);
	}

	@Intrinsic(displace = true)
	public void port_lib$i$applyComponents(DataComponentMap components) {
		this.applyComponents(components);
	}
}

package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.item.XpRepairItem;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
	@Shadow
	private int value;

	@Unique
	private ItemStack port_lib$stackToMend = null;

	public ExperienceOrbMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(
			method = "tick",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float port_lib$setFriction(float original) {
		BlockPos pos = BlockPos.containing(getX(), getY() - 1, getZ());
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level, pos, this);
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;",
					remap = false
			)
	)
	private Object port_lib$grabStackToMend(Object value) {
		if (value instanceof ItemStack stack) {
			this.port_lib$stackToMend = stack;
		}
		return value;
	}

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/ExperienceOrb;xpToDurability(I)I"
			)
	)
	private int port_lib$modifyRepairAmount(int durability) {
		ItemStack stack = port_lib$stackToMend;
		// set to null after we've used it
		port_lib$stackToMend = null;
		if (stack != null && stack.getItem() instanceof XpRepairItem custom) {
			float ratio = custom.getXpRepairRatio(stack);
			return (int) ratio * this.value;
		}
		return durability;
	}

	@Inject(method = "playerTouch", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 0), cancellable = true)
	private void port_lib$onPlayerPickupXp(Player player, CallbackInfo ci) {
		PlayerEvents.PickupXp pickupXp = new PlayerEvents.PickupXp(player, MixinHelper.cast(this));
		pickupXp.sendEvent();
		if (pickupXp.isCanceled())
			ci.cancel();
	}
}

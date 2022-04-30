package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.util.XpRepairItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
	@Shadow
	private int value;

	@Shadow
	protected abstract int durabilityToXp(int durability);

	@Shadow
	protected abstract int repairPlayerItems(Player player, int amount);

	public ExperienceOrbMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@ModifyVariable(
			method = "tick",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	private float port_lib$setFriction(float original) {
		BlockPos pos = new BlockPos(getX(), getY() - 1, getZ());
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, level, pos, this);
		}
		return original;
	}

	@Inject(method = "repairPlayerItems", at = @At(value = "INVOKE", target = "Ljava/util/Map$Entry;getValue()Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$XpRepairRatio(Player player, int amount, CallbackInfoReturnable<Integer> cir, Map.Entry<EquipmentSlot, ItemStack> entry) {
		if(entry.getValue().getItem() instanceof XpRepairItem xpRepairItem) {
			ItemStack itemstack = entry.getValue();
			int i = Math.min((int) (this.value * xpRepairItem.getXpRepairRatio(itemstack)), itemstack.getDamageValue());
			itemstack.setDamageValue(itemstack.getDamageValue() - i);
			int j = amount - this.durabilityToXp(i);
			cir.setReturnValue(j > 0 ? this.repairPlayerItems(player, j) : 0);
		}
	}
}

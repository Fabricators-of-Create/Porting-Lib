package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.XpRepairItem;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
	@Shadow
	private int value;

	@Unique
	private ThreadLocal<ItemStack> port_lib$stackToMend = new ThreadLocal<>();

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
			this.port_lib$stackToMend.set(stack);
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
		ItemStack stack = port_lib$stackToMend.get();
		port_lib$stackToMend.remove();
		// set to null after we've used it
		port_lib$stackToMend = null;
		if (stack != null && stack.getItem() instanceof XpRepairItem custom) {
			float ratio = custom.getXpRepairRatio(stack);
			return (int) ratio * this.value;
		}
		return durability;
	}
}

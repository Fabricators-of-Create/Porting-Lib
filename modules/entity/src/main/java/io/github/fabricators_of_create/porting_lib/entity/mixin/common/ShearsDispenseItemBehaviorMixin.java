package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.entity.extensions.IShearable;
import io.github.fabricators_of_create.porting_lib.entity.extensions.VanillaIShearable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.Shearable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsDispenseItemBehavior.class)
public class ShearsDispenseItemBehaviorMixin {
	private static final ThreadLocal<ItemStack> port_lib$passed_stack = new ThreadLocal<>();

	@Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/ShearsDispenseItemBehavior;tryShearLivingEntity(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)Z"))
	private void setPassedStack(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
		port_lib$passed_stack.set(item);
	}

	@Definition(id = "livingEntity", local = @Local(type = LivingEntity.class))
	@Definition(id = "Shearable", type = Shearable.class)
	@Expression("livingEntity instanceof Shearable")
	@ModifyExpressionValue(method = "tryShearLivingEntity", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean supportIShearable(boolean original, ServerLevel level, BlockPos pos, @Local LivingEntity livingEntity) {
		if (livingEntity instanceof IShearable shearable && !(livingEntity instanceof VanillaIShearable)) {
			shearable.onSheared(null, port_lib$passed_stack.get(), level, pos)
					.forEach(drop -> shearable.spawnShearedDrop(level, pos, drop));
			level.gameEvent(null, GameEvent.SHEAR, pos);
		}

		return original;
	}

	@Inject(method = "tryShearLivingEntity", at = @At("TAIL"))
	private static void freeItemStack(ServerLevel level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		port_lib$passed_stack.set(null);
	}
}

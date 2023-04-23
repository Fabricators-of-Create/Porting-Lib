package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;

	// TODO: port? this breaks stuff
	@Inject(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0))
	private void fixMiss(float tickDelta, CallbackInfo ci, @Local(index = 5) Vec3 vec3, @Local(index = 8) double d1) {
//		if (this.minecraft.hitResult.getType() != HitResult.Type.MISS) {
//			double blockReach = this.minecraft.player.getBlockReach();
//			// Discard the result as a miss if it is outside the block reach.
//			if (d1 > blockReach * blockReach) {
//				Vec3 pos = this.minecraft.hitResult.getLocation();
//				this.minecraft.hitResult = BlockHitResult.miss(pos, Direction.getNearest(vec3.x, vec3.y, vec3.z), BlockPos.containing(pos));
//			}
//		}
	}

	@ModifyExpressionValue(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasFarPickRange()Z"))
	private boolean disablePickRange(boolean original) {
		return false;
	}

	@ModifyArg(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"), index = 0)
	private double modifyPickRange(double d0) {
		return Math.max(d0, this.minecraft.player.getEntityReach());
	}

	@WrapOperation(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D", ordinal = 0))
	private double dontChangeRange(Vec3 instance, Vec3 vec, Operation<Double> operation, @Local(index = 8) double e) {
		if (this.minecraft.hitResult.getType() != HitResult.Type.MISS)
			return operation.call(instance, vec);
		return e;
	}
}

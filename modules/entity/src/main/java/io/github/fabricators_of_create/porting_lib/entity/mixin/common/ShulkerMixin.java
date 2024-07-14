package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityTeleportEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.monster.Shulker;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Shulker.class)
public class ShulkerMixin {
	@ModifyVariable(method = "teleportSomewhere", at = @At(value = "JUMP", opcode = Opcodes.IFNULL), index = 4)
	private Direction onEnderTeleport(Direction direction, @Local(index = 3) BlockPos pos, @Share("event") LocalRef<EntityTeleportEvent.EnderEntity> eventRef) {
		if (direction != null) {
			EntityTeleportEvent.EnderEntity event = EntityHooks.onEnderTeleport((Shulker) (Object) this, pos.getX(), pos.getY(), pos.getZ());
			eventRef.set(event);
			if (event.isCanceled())
				return null;
		}
		return direction;
	}

	@ModifyVariable(method = "teleportSomewhere", at = @At(value = "JUMP", opcode = Opcodes.IFNULL, by = 1), index = 3) // Shift by 1 so we apply after the above code
	private BlockPos changeTeleportTarget(BlockPos pos, @Local(index = 4) Direction direction, @Share("event") LocalRef<EntityTeleportEvent.EnderEntity> eventRef) {
		if (direction != null) {
			EntityTeleportEvent.EnderEntity event = eventRef.get();
			return BlockPos.containing(event.getTargetX(), event.getTargetY(), event.getTargetZ());
		}
		return pos;
	}
}

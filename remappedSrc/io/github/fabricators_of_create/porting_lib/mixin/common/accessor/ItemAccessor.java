package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {
	@Invoker("getPlayerPOVHitResult")
	static BlockHitResult port_lib$getPlayerPOVHitResult(World level, PlayerEntity player, RaycastContext.FluidHandling fluidMode) {
		throw new RuntimeException("mixin failed!");
	}
}

package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Item.class)
public class ItemMixin {
	@ModifyConstant(method = "getPlayerPOVHitResult", constant = @Constant(doubleValue = 5.0))
	private static double modifyBlockReach(double constant, Level world, Player player) {
		return player.getBlockReach();
	}
}

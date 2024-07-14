package io.github.fabricators_of_create.porting_lib.entity.ext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface PlayerExt {
	default float getDigSpeed(BlockState state, @Nullable BlockPos pos) {
		setDigSpeedContext(pos);
		return ((Player) this).getDestroySpeed(state);
	}

	default void setDigSpeedContext(@Nullable BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}

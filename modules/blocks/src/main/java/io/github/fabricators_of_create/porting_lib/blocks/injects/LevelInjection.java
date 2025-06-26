package io.github.fabricators_of_create.porting_lib.blocks.injects;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collection;

public interface LevelInjection {
	default void port_lib$addFreshBlockEntities(Collection<BlockEntity> beList) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}

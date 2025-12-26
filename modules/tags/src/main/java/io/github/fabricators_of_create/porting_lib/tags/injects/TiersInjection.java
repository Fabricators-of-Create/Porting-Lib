package io.github.fabricators_of_create.porting_lib.tags.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jspecify.annotations.Nullable;

public interface TiersInjection {
	@Nullable
	default TagKey<Block> port_lib$getTag() {
		throw PortingLib.createMixinException("TiersInjection.port_lib$getTag()");
	}
}

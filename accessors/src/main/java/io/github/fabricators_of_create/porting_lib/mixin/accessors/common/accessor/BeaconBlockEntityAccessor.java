package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
	@Accessor("beamSections")
	List<BeaconBlockEntity.BeaconBeamSection> port_lib$getBeamSections();
}

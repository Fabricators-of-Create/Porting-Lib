package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.List;
import net.minecraft.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
	@Accessor("beamSections")
	List<BeaconBlockEntity.BeamSegment> port_lib$getBeamSections();
}

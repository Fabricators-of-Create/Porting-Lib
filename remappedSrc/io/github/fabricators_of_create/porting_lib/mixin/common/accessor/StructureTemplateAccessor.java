package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Structure.class)
public interface StructureTemplateAccessor {
	@Invoker("createEntityIgnoreException")
	static Optional<Entity> port_lib$createEntityIgnoreException(ServerWorldAccess iServerWorld, NbtCompound compoundNBT) {
		throw new AssertionError("Mixin application failed!");
	}
}

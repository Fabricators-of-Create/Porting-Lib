package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Painting.class)
public interface PaintingAccessor {
	@Invoker("setVariant")
	void porting_lib$setVariant(Holder<PaintingVariant> variant);
}

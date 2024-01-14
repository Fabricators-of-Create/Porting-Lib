package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import net.minecraft.client.model.AgeableListModel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableListModel.class)
public interface AgeableListModelAccessor {
	@Accessor("scaleHead")
	boolean porting_lib$scaleHead();

	@Accessor("babyYHeadOffset")
	float porting_lib$babyYHeadOffset();

	@Accessor("babyZHeadOffset")
	float porting_lib$babyZHeadOffset();

	@Accessor("babyHeadScale")
	float porting_lib$babyHeadScale();

	@Accessor("babyBodyScale")
	float porting_lib$babyBodyScale();

	@Accessor("bodyYOffset")
	float porting_lib$bodyYOffset();
}

package io.github.fabricators_of_create.porting_lib.milk.data;

import io.github.fabricators_of_create.porting_lib.milk.PortingLibMilk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class MilkTagProvider extends FabricTagProvider.FluidTagProvider {
	public MilkTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider wrapperLookup) {
		getOrCreateTagBuilder(ConventionalFluidTags.MILK).addOptional(PortingLibMilk.MILK.getId()).addOptional(PortingLibMilk.FLOWING_MILK.getId());
	}
}

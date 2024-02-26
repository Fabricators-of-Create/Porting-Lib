package io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;

@Mixin(AdvancementProvider.class)
public interface AdvancementProviderAccessor {
	@Accessor
	PackOutput.PathProvider getPathProvider();

	@Accessor
	List<AdvancementSubProvider> getSubProviders();
}

package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Consumer;

@Mixin(AdvancementProvider.class)
public interface AdvancementProviderAccessor {
	@Accessor
	DataGenerator.PathProvider getPathProvider();

	@Accessor
	List<Consumer<Consumer<Advancement>>> getTabs();
}

package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TagContainer.class)
public interface TagContainerAccessor {
	@Accessor("collections")
	Map<ResourceKey<? extends Registry<?>>, TagCollection<?>> port_lib$collections();
}

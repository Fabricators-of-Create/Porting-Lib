package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.TagAppenderExtensions;
import net.minecraft.data.tags.TagsProvider;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(TagsProvider.TagAppender.class)
public class TagAppenderMixin<T> implements TagAppenderExtensions<T> {
}

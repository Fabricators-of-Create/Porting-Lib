package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.TagAppenderExtension;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder;
import net.minecraft.data.tags.TagsProvider;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagsProvider.TagAppender.class)
public abstract class TagAppenderMixin<T> implements TagAppenderExtension<T> {
	@Shadow
	public abstract TagAppender<T> addTag(TagKey<T> tag);

	// generics are a mess
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	@Override
	public TagAppender<T> addTags(TagKey<T>... values) {
		if ((Object) this instanceof FabricTagBuilder fabricTagBuilder) {
			for (TagKey<T> value : values) {
				fabricTagBuilder.forceAddTag(value);
			}
		} else {
			for (TagKey<T> value : values) {
				addTag(value);
			}
		}
		return (TagAppender<T>) (Object) this;
	}
}

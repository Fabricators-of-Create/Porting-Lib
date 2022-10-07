package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.TagAppenderExtensions;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FabricTagBuilder;
import net.minecraft.data.tags.TagsProvider;

import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.tags.TagKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TagsProvider.TagAppender.class)
public abstract class TagAppenderMixin<T> implements TagAppenderExtensions {
	@Shadow
	public abstract TagAppender<T> addTag(TagKey<T> tag);

	// generics are a mess
	@SuppressWarnings({"unchecked", "ConstantConditions", "rawtypes"})
	@Override
	public <E> TagAppender<E> addTags(TagKey<E>... values) {
		if ((Object) this instanceof FabricTagBuilder fabricTagBuilder) {
			for (TagKey<E> value : values) {
				fabricTagBuilder.forceAddTag(value);
			}
		} else {
			for (TagKey<E> value : values) {
				addTag((TagKey) value);
			}
		}
		return (TagAppender<E>) (Object) this;
	}
}

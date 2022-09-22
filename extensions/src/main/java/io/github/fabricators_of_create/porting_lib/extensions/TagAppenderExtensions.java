package io.github.fabricators_of_create.porting_lib.extensions;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtensions<T> {
	@SuppressWarnings("unchecked")
	default TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
		TagsProvider.TagAppender<T> builder = (TagsProvider.TagAppender<T>) this;
		if (builder instanceof FabricTagProvider<T>.FabricTagBuilder<T> fabricTagBuilder)
			for (TagKey<T> value : values) {
				fabricTagBuilder.forceAddTag(value);
			}
		else
			for (TagKey<T> value : values) {
				builder.addTag(value);
			}
		return builder;
	}
}

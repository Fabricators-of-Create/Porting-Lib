package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtensions<T> {
	@SuppressWarnings("unchecked")
	default TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
		TagsProvider.TagAppender<T> builder = (TagsProvider.TagAppender<T>) this;
		for (TagKey<T> value : values) {
			builder.addTag(value);
		}
		return builder;
	}
}

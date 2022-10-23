package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtensions {
	@SuppressWarnings("unchecked")
	default <E> TagsProvider.TagAppender<E> addTags(TagKey<E>... values) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}

package io.github.fabricators_of_create.porting_lib.extensions.common;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtension<T> {
	@SuppressWarnings("unchecked")
	default TagsProvider.TagAppender<T> addTags(TagKey<T>... values) {
		throw PortingLib.createMixinException("TagAppenderExtension.addTags(TagKey<T>...)");
	}
}

package io.github.fabricators_of_create.porting_lib.extensions.common;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtension<E, T> {
	/**
	 * @see TagAppender#addTag(TagKey)
	 */
	@SuppressWarnings("unchecked")
	default TagAppender<E, T> addTags(TagKey<T>... values) {
		throw PortingLib.createMixinException("TagAppenderExtension.addTags");
	}

	/**
	 * @see TagAppender#addOptionalTag(TagKey)
	 */
	default TagAppender<E, T> addOptionalTags(TagKey<T>... values) {
		throw PortingLib.createMixinException("TagAppenderExtension.addOptionalTags");
	}
}

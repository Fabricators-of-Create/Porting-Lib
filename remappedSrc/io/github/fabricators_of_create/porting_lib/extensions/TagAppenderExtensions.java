package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tags.TagKey;

public interface TagAppenderExtensions<T> {
	@SuppressWarnings("unchecked")
	default AbstractTagProvider.ObjectBuilder<T> addTags(TagKey<T>... values) {
		AbstractTagProvider.ObjectBuilder<T> builder = (AbstractTagProvider.ObjectBuilder<T>) this;
		for (TagKey<T> value : values) {
			builder.addTag(value);
		}
		return builder;
	}
}

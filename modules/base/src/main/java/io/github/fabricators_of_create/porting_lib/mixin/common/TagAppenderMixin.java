package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.TagAppenderExtension;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricProvidedTagBuilder;
import net.minecraft.data.tags.TagAppender;

import net.minecraft.tags.TagKey;

import org.spongepowered.asm.mixin.*;

@SuppressWarnings({"NullableProblems", "unchecked"})
@Mixin(TagAppender.class)
@Implements(@Interface(iface = TagAppenderExtension.class, prefix = "port_lib$"))
public interface TagAppenderMixin<E, T> extends FabricProvidedTagBuilder<E, T> {
	@Shadow
	TagAppender<E, T> addOptionalTag(TagKey<T> tag);

	// generics are a mess
	@Intrinsic
	default TagAppender<E, T> port_lib$addTags(TagKey<T>... values) {
		for (TagKey<T> value : values) {
			forceAddTag(value);
		}

		return (TagAppender<E, T>) this;
	}

	@Intrinsic
	default TagAppender<E, T> port_lib$addOptionalTags(TagKey<T>... values) {
		for (TagKey<T> value : values) {
			addOptionalTag(value);
		}

		return (TagAppender<E, T>) this;
	}
}

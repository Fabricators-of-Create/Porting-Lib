package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.TagAppenderExtensions;
import net.minecraft.data.server.AbstractTagProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractTagProvider.ObjectBuilder.class)
public abstract class TagAppenderMixin<T> implements TagAppenderExtensions<T> {
}

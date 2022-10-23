package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ResourceLocationExtensions;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements ResourceLocationExtensions {
	@Shadow
	@Final
	protected String namespace;

	@Shadow
	@Final
	protected String path;

	@Override
	public int compareNamespaced(ResourceLocation o) {
		int ret = namespace.compareTo(o.getNamespace());
		return ret != 0 ? ret : this.path.compareTo(o.getPath());
	}
}

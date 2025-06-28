package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.ResourceLocationExtension;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements ResourceLocationExtension {
	@Shadow
	@Final
	private String namespace;

	@Shadow
	@Final
	private String path;

	@Override
	public int port_lib$compareNamespaced(ResourceLocation o) {
		int ret = this.namespace.compareTo(o.getNamespace());
		return ret != 0 ? ret : this.path.compareTo(o.getPath());
	}
}

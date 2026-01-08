package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.common.IdentifierExtension;

import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;

@Mixin(Identifier.class)
@Implements(@Interface(iface = IdentifierExtension.class, prefix = "port_lib$"))
public abstract class IdentifierMixin implements IdentifierExtension {
	@Shadow
	@Final
	private String namespace;

	@Shadow
	@Final
	private String path;

	@Intrinsic
	public int port_lib$compareNamespaced(Identifier o) {
		int ret = this.namespace.compareTo(o.getNamespace());
		return ret != 0 ? ret : this.path.compareTo(o.getPath());
	}
}

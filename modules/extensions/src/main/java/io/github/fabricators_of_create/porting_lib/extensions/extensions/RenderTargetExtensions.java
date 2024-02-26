package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderTargetExtensions {
	/**
	 * Attempts to enable 8 bits of stencil buffer on this FrameBuffer.
	 * Modders must call this directly to set things up.
	 * This is to prevent the default cause where graphics cards do not support stencil bits.
	 * <b>Make sure to call this on the main render thread!</b>
	 */
	default void enableStencil() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/** @see RenderTargetExtensions#enableStencil() */
	default void disableStencil() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * Returns whether this FBO has been successfully initialized with stencil bits.
	 * If not, and a modder wishes it to be, they must call enableStencil.
	 */
	default boolean isStencilEnabled() {
		return false;
	}
}

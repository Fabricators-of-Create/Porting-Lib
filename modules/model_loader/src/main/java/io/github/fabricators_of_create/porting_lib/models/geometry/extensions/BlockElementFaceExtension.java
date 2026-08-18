package io.github.fabricators_of_create.porting_lib.models.geometry.extensions;

import javax.annotation.Nullable;

import org.apache.commons.lang3.mutable.MutableObject;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.ExtraFaceData;
import net.minecraft.client.renderer.block.model.BlockElement;

public interface BlockElementFaceExtension {
	default MutableObject<BlockElement> port_lib$parent() {
		throw PortingLib.createMixinException("BlockElementExt#port_lib$parent()");
	}

	default void port_lib$setFaceData(@Nullable ExtraFaceData faceData, MutableObject<BlockElement> parent) {
		throw PortingLib.createMixinException("BlockElementExt#port_lib$setFaceData(ExtraFaceData, MutableObject<BlockElement>)");
	}

	default ExtraFaceData port_lib$faceData() {
		throw PortingLib.createMixinException("BlockElementExt#faceData()");
	}
}

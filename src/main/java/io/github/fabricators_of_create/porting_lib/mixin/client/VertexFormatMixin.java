package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.fabricators_of_create.porting_lib.extensions.VertexFormatExtensions;
import it.unimi.dsi.fastutil.ints.IntList;

@Mixin(VertexFormat.class)
public abstract class VertexFormatMixin implements VertexFormatExtensions {

	@Shadow
	@Final
	private IntList offsets;

	@Unique
	@Override
	public int getOffset(int index) {
		return offsets.getInt(index);
	}
}

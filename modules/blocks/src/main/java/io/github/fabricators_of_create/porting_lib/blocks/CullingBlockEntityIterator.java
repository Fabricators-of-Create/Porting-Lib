package io.github.fabricators_of_create.porting_lib.blocks;

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomBoundingBoxBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CullingBlockEntityIterator implements Iterator<BlockEntity> {
	private final BlockEntityRenderDispatcher dispatcher;
	private final Iterator<? extends BlockEntity> wrapped;
	private final Frustum frustum;

	private BlockEntity next;
	private boolean nextChecked;

	public CullingBlockEntityIterator(BlockEntityRenderDispatcher dispatcher, Iterator<? extends BlockEntity> iterator, Frustum frustum) {
		this.dispatcher = dispatcher;
		this.wrapped = iterator;
		this.frustum = frustum;
	}

	@Override
	public boolean hasNext() {
		ensureNextChecked();
		return next != null;
	}

	@Override
	public BlockEntity next() {
		ensureNextChecked();
		if (next == null) {
			throw new NoSuchElementException();
		}
		nextChecked = false;
		return next;
	}

	@Override
	public void remove() {
		wrapped.remove();
	}

	private void ensureNextChecked() {
		if (!nextChecked) {
			next = nextCulled();
			nextChecked = true;
		}
	}

	private BlockEntity nextCulled() {
		while (true) {
			if (wrapped.hasNext()) {
				BlockEntity next = wrapped.next();
				BlockEntityRenderer<? extends BlockEntity> renderer = dispatcher.getRenderer(next);
				if (renderer instanceof CustomBoundingBoxBlockEntityRenderer cullable) {
					if (frustum.isVisible(cullable.getRenderBoundingBox(next))) {
						return next;
					}
				} else {
					return next;
				}
			} else {
				return null;
			}
		}
	}
}

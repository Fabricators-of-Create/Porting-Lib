package io.github.fabricators_of_create.porting_lib.world;

import java.util.Iterator;
import java.util.NoSuchElementException;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

public class PieceBeardifierIterator implements Iterator<StructurePiece> {
	private final Iterator<? extends StructurePiece> wrapped;
	private final ObjectList<Beardifier.Rigid> rigids;

	private StructurePiece next;
	private boolean nextChecked;

	public PieceBeardifierIterator(Iterator<? extends StructurePiece> iterator, ObjectList<Beardifier.Rigid> rigids) {
		wrapped = iterator;
		this.rigids = rigids;
	}

	@Override
	public boolean hasNext() {
		ensureNextChecked();
		return next != null;
	}

	@Override
	public StructurePiece next() {
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
			next = nextPiece();
			nextChecked = true;
		}
	}

	private StructurePiece nextPiece() {
		while (true) {
			if (wrapped.hasNext()) {
				StructurePiece next = wrapped.next();
				if (next instanceof PieceBeardifierModifier modifier) {
					if (modifier.getTerrainAdjustment() != TerrainAdjustment.NONE) {
						rigids.add(new Beardifier.Rigid(modifier.getBeardifierBox(), modifier.getTerrainAdjustment(), modifier.getGroundLevelDelta()));
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

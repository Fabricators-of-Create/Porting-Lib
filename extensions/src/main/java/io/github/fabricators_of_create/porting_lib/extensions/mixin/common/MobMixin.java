package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.MobExtensions;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class MobMixin implements MobExtensions {
	private boolean spawnCancelled = false;

	@Override
	public final boolean isSpawnCancelled() {
		return this.spawnCancelled;
	}

	@Override
	public final void setSpawnCancelled(boolean cancel) {
		this.spawnCancelled = cancel;
	}
}

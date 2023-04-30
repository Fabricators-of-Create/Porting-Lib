package io.github.fabricators_of_create.porting_lib.entity.events.living;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public interface MobEntitySetTargetCallback {
	Event<MobEntitySetTargetCallback> EVENT = EventFactory.createArrayBacked(MobEntitySetTargetCallback.class, callbacks -> (targeting, target) -> {
		for (MobEntitySetTargetCallback callback : callbacks) {
			callback.onMobEntitySetTarget(targeting, target);
		}
	});

	void onMobEntitySetTarget(Mob targeting, @Nullable LivingEntity target);
}

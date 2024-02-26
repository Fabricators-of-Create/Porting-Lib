package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

public interface MobEntitySetTargetCallback {
	Event<MobEntitySetTargetCallback> EVENT = EventFactory.createArrayBacked(MobEntitySetTargetCallback.class, callbacks -> (targeting, target) -> {
		for (MobEntitySetTargetCallback callback : callbacks) {
			callback.onMobEntitySetTarget(targeting, target);
		}
	});

	void onMobEntitySetTarget(LivingEntity targeting, LivingEntity target);
}

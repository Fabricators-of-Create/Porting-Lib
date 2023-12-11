package io.github.fabricators_of_create.porting_lib.entity.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

public class EntityStruckByLightningEvent extends EntityEvents {
	private final LightningBolt lightning;

	public EntityStruckByLightningEvent(Entity entity, LightningBolt lightning) {
		super(entity);
		this.lightning = lightning;
	}

	public LightningBolt getLightning() {
		return lightning;
	}

	@Override
	public void sendEvent() {
		ENTITY_STRUCK_BY_LIGHTING.invoker().onEntityStruckByLightning(this);
	}
}

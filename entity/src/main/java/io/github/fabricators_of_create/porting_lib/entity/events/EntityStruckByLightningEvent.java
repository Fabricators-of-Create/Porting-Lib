package io.github.fabricators_of_create.porting_lib.entity.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightening.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * {@link LightningBolt#tick()}.<br>
 * <br>
 * {@link #lightning} contains the instance of EntityLightningBolt attempting to strike an entity.<br>
 * <br>
 * If this event is canceled, the Entity is not struck by the lightening.<br>
 * <br>
 **/
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

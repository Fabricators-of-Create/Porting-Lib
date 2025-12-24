package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired before the player's arm is rendered in first person. This is a more targeted version of {@link RenderHandEvent},
 * and can be used to replace the rendering of the player's arm, such as for rendering armor on the arm or outright
 * replacing the arm with armor.
 *
 * <p>This event is {@linkplain CancellableEvent cancellable}.
 * If this event is cancelled, then the arm will not be rendered.</p>
 *
 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 */
public class RenderArmEvent extends BaseEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onRenderArm(event);
	});

	private final PoseStack poseStack;
	private final SubmitNodeCollector submitNodeCollector;
	private final int packedLight;
	private final AbstractClientPlayer player;
	private final HumanoidArm arm;

	@ApiStatus.Internal
	public RenderArmEvent(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AbstractClientPlayer player, HumanoidArm arm) {
		this.poseStack = poseStack;
		this.submitNodeCollector = submitNodeCollector;
		this.packedLight = packedLight;
		this.player = player;
		this.arm = arm;
	}

	/**
	 * {@return the arm being rendered}
	 */
	public HumanoidArm getArm() {
		return arm;
	}

	/**
	 * {@return the pose stack used for rendering}
	 */
	public PoseStack getPoseStack() {
		return poseStack;
	}

	/**
	 * {@return the submit node collector}
	 */
	public SubmitNodeCollector getSubmitNodeCollector() {
		return submitNodeCollector;
	}

	/**
	 * {@return the amount of packed (sky and block) light for rendering}
	 *
	 * @see LightTexture
	 */
	public int getPackedLight() {
		return packedLight;
	}

	/**
	 * {@return the client player that is having their arm rendered} In general, this will be the same as
	 * {@link net.minecraft.client.Minecraft#player}.
	 */
	public AbstractClientPlayer getPlayer() {
		return player;
	}

	@Override
	public RenderArmEvent sendEvent() {
		EVENT.invoker().onRenderArm(this);
		return this;
	}

	public interface Callback {
		void onRenderArm(RenderArmEvent event);
	}
}

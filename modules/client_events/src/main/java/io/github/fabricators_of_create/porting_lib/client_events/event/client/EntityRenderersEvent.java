package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.player.Player;

import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Fired for on different events/actions relating to {@linkplain EntityRenderer entity renderers}.
 * See the various subclasses for listening to different events.
 *
 * <p>These events are fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 *
 * @see EntityRenderersEvent.AddLayers
 */
public abstract class EntityRenderersEvent extends BaseEvent {
	@ApiStatus.Internal
	protected EntityRenderersEvent() {}

	/**
	 * Fired for registering entity renderer layers at the appropriate time, after the entity and player renderers maps
	 * have been created.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class AddLayers extends EntityRenderersEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onAddLayers(event);
			}
		});

		public interface Callback {
			void onAddLayers(AddLayers event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onAddLayers(this);
		}

		private final Map<EntityType<?>, EntityRenderer<?>> renderers;
		private final Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap;
		private final EntityRendererProvider.Context context;

		@ApiStatus.Internal
		public AddLayers(Map<EntityType<?>, EntityRenderer<?>> renderers, Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers, EntityRendererProvider.Context context) {
			this.renderers = renderers;
			this.skinMap = playerRenderers;
			this.context = context;
		}

		/**
		 * {@return the set of player skin names which have a renderer}
		 * <p>
		 * Minecraft provides two default skin names: {@code default} for the
		 * {@linkplain ModelLayers#PLAYER regular player model} and {@code slim} for the
		 * {@linkplain ModelLayers#PLAYER_SLIM slim player model}.
		 */
		public Set<PlayerSkin.Model> getSkins() {
			return skinMap.keySet();
		}

		/**
		 * Returns a player skin renderer for the given skin name.
		 *
		 * @param skinModel the skin model to get the renderer for
		 * @param <R>       the type of the skin renderer, usually {@link PlayerRenderer}
		 * @return the skin renderer, or {@code null} if no renderer is registered for that skin name
		 * @see #getSkins()
		 */
		@Nullable
		@SuppressWarnings("unchecked")
		public <R extends EntityRenderer<? extends Player>> R getSkin(PlayerSkin.Model skinModel) {
			return (R) skinMap.get(skinModel);
		}

		/**
		 * {@return the set of entity types which have a renderer}
		 */
		public Set<EntityType<?>> getEntityTypes() {
			return renderers.keySet();
		}

		/**
		 * Returns an entity renderer for the given entity type. Note that the returned renderer may not be a
		 * {@link LivingEntityRenderer}.
		 *
		 * @param entityType the entity type to return a renderer for
		 * @param <T>        the type of entity the renderer is for
		 * @param <R>        the type of the renderer
		 * @return the renderer, or {@code null} if no renderer is registered for that entity type
		 */
		@Nullable
		@SuppressWarnings("unchecked")
		public <T extends Entity, R extends EntityRenderer<T>> R getRenderer(EntityType<? extends T> entityType) {
			return (R) renderers.get(entityType);
		}

		/**
		 * {@return the set of entity models}
		 */
		public EntityModelSet getEntityModels() {
			return this.context.getModelSet();
		}

		/**
		 * {@return the context for the entity renderer provider}
		 */
		public EntityRendererProvider.Context getContext() {
			return context;
		}
	}

	/**
	 * Fired for registering additional {@linkplain SkullModelBase skull models} at the appropriate time.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class CreateSkullModels extends EntityRenderersEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onSkullModelsCreated(event);
			}
		});

		public interface Callback {
			void onSkullModelsCreated(CreateSkullModels event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onSkullModelsCreated(this);
		}

		private final ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder;
		private final EntityModelSet entityModelSet;

		@ApiStatus.Internal
		public CreateSkullModels(ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder, EntityModelSet entityModelSet) {
			this.builder = builder;
			this.entityModelSet = entityModelSet;
		}

		/**
		 * {@return the set of entity models}
		 */
		public EntityModelSet getEntityModelSet() {
			return entityModelSet;
		}

		/**
		 * Registers the constructor for a skull block with the given {@link SkullBlock.Type}.
		 * These will be inserted into the maps used by the item, entity, and block model renderers at the appropriate
		 * time.
		 *
		 * @param type  a unique skull type; an exception will be thrown later if multiple mods (including vanilla)
		 *              register models for the same type
		 * @param model the skull model instance. A typical implementation will simply bake a model using
		 *              {@link EntityModelSet#bakeLayer(ModelLayerLocation)} and pass it to the constructor for
		 *              {@link SkullModel}.
		 */
		public void registerSkullModel(SkullBlock.Type type, SkullModelBase model) {
			builder.put(type, model);
		}
	}
}

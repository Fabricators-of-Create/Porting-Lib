package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.object.skull.SkullModel;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.level.block.SkullBlock;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
	 * <p>This event is fired on the mod-specific event bus,
	 * only on the {@linkplain EnvType#CLIENT logical client}.</p>
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
		public AddLayers sendEvent() {
			EVENT.invoker().onAddLayers(this);
			return this;
		}

		private final Map<EntityType<?>, EntityRenderer<?, ?>> renderers;
		private final Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> playerRenderers;
		private final Map<PlayerModelType, AvatarRenderer<ClientMannequin>> mannequinRenderers;
		private final EntityRendererProvider.Context context;

		@ApiStatus.Internal
		public AddLayers(
				Map<EntityType<?>, EntityRenderer<?, ?>> renderers,
				Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> playerRenderers,
				Map<PlayerModelType, AvatarRenderer<ClientMannequin>> mannequinRenderers,
				EntityRendererProvider.Context context) {
			this.renderers = renderers;
			this.playerRenderers = playerRenderers;
			this.mannequinRenderers = mannequinRenderers;
			this.context = context;
		}

		/**
		 * {@return the set of player skin names which have a renderer}
		 * <p>
		 * Minecraft provides two default skin names: {@code default} for the
		 * {@linkplain ModelLayers#PLAYER regular player model} and {@code slim} for the
		 * {@linkplain ModelLayers#PLAYER_SLIM slim player model}.
		 */
		public Set<PlayerModelType> getSkins() {
			return playerRenderers.keySet();
		}

		/**
		 * Returns a player skin renderer for the given skin name.
		 *
		 * @param skinModel the skin model to get the renderer for
		 * @param <R>       the type of the skin renderer, usually {@link AvatarRenderer}
		 * @return the skin renderer, or {@code null} if no renderer is registered for that skin name
		 * @see #getSkins()
		 */
		@org.jspecify.annotations.Nullable
		@SuppressWarnings("unchecked")
		public <R extends AvatarRenderer<AbstractClientPlayer>> R getPlayerRenderer(PlayerModelType skinModel) {
			return (R) playerRenderers.get(skinModel);
		}

		/**
		 * Returns a player skin renderer for the given skin name.
		 *
		 * @param skinModel the skin model to get the renderer for
		 * @param <R>       the type of the skin renderer, usually {@link AvatarRenderer}
		 * @return the skin renderer, or {@code null} if no renderer is registered for that skin name
		 * @see #getSkins()
		 */
		@org.jspecify.annotations.Nullable
		@SuppressWarnings("unchecked")
		public <R extends AvatarRenderer<ClientMannequin>> R getMannequinRenderer(PlayerModelType skinModel) {
			return (R) mannequinRenderers.get(skinModel);
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
		public <T extends Entity, R extends EntityRenderer<T, ?>> R getRenderer(EntityType<? extends T> entityType) {
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
	 * Fired for registering additional {@linkplain SkullModelBase skull models}.
	 *
	 * <p>This event is fired on the mod-specific event bus,
	 * only on the {@linkplain EnvType#CLIENT logical client}.</p>
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
		public CreateSkullModels sendEvent() {
			EVENT.invoker().onSkullModelsCreated(this);
			return this;
		}

		private final Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> skullModels;
		private final Map<SkullBlock.Type, Identifier> skullTextures;

		@ApiStatus.Internal
		public CreateSkullModels(Map<SkullBlock.Type, Function<EntityModelSet, SkullModelBase>> skullModels, Map<SkullBlock.Type, Identifier> skullTextures) {
			this.skullModels = skullModels;
			this.skullTextures = skullTextures;
		}

		/**
		 * Registers a {@link SkullModel} for a skull block with the given {@link SkullBlock.Type}, and optionally registers a skull texture to the {@link SkullBlockRenderer#SKIN_BY_TYPE} map.
		 *
		 * @param type          a unique skull type; an exception will be thrown if multiple mods register models
		 *                      for the same type or a mod tries to register a model for a vanilla type
		 * @param layerLocation the key that identifies the {@link LayerDefinition} used by the model
		 * @param skullTexture  the skull texture to put in the {@link SkullBlockRenderer#SKIN_BY_TYPE} map, if provided.
		 */
		public void registerSkullModel(SkullBlock.Type type, ModelLayerLocation layerLocation, @org.jspecify.annotations.Nullable Identifier skullTexture) {
			this.registerSkullModel(type, layerLocation, SkullModel::new, skullTexture);
		}

		/**
		 * Registers the entity model for a skull block with the given {@link SkullBlock.Type}, and optionally registers a skull texture to the {@link SkullBlockRenderer#SKIN_BY_TYPE} map.
		 *
		 * @param type          a unique skull type; an exception will be thrown if multiple mods register models
		 *                      for the same type or a mod tries to register a model for a vanilla type
		 * @param layerLocation the key that identifies the {@link LayerDefinition} used by the model
		 * @param factory       the factory to create the skull model instance, taking in the root {@link ModelPart} and
		 *                      returning the model.
		 * @param skullTexture  the skull texture to put in the {@link SkullBlockRenderer#SKIN_BY_TYPE} map, if provided.
		 */
		public void registerSkullModel(SkullBlock.Type type, ModelLayerLocation layerLocation, Function<ModelPart, SkullModelBase> factory, @org.jspecify.annotations.Nullable Identifier skullTexture) {
			this.registerSkullModel(type, modelSet -> factory.apply(modelSet.bakeLayer(layerLocation)), skullTexture);
		}

		/**
		 * Registers the entity model for a skull block with the given {@link SkullBlock.Type}, and optionally registers a skull texture to the {@link SkullBlockRenderer#SKIN_BY_TYPE} map.
		 *
		 * @param type         a unique skull type; an exception will be thrown if multiple mods register models for
		 *                     the same type or a mod tries to register a model for a vanilla type
		 * @param factory      the factory to create the skull model instance. A typical implementation will simply bake
		 *                     a model using {@link EntityModelSet#bakeLayer(ModelLayerLocation)} and pass it to the
		 *                     constructor for {@link SkullModel}
		 * @param skullTexture the skull texture to put in the {@link SkullBlockRenderer#SKIN_BY_TYPE} map, if provided.
		 */
		public void registerSkullModel(SkullBlock.Type type, Function<EntityModelSet, SkullModelBase> factory, @org.jspecify.annotations.Nullable Identifier skullTexture) {
			if (type instanceof SkullBlock.Types) {
				throw new IllegalArgumentException("Cannot register skull model for vanilla skull type: " + type.getSerializedName());
			}
			if (skullModels.putIfAbsent(type, factory) != null) {
				throw new IllegalArgumentException("Factory already registered for provided skull type: " + type.getSerializedName());
			}
			if (skullTexture == null) return;
			if (skullTextures.putIfAbsent(type, skullTexture) != null) {
				throw new IllegalArgumentException("Texture already registered for provided skull type: " + type.getSerializedName());
			}
		}
	}
}

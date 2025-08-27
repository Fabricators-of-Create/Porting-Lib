package io.github.fabricators_of_create.porting_lib.models.events.client;

import com.google.common.base.Preconditions;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.models.PortingLibModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;

import net.minecraft.client.resources.model.ModelResourceLocation;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Houses events related to models.
 */
public abstract class ModelEvent extends BaseEvent {
	@ApiStatus.Internal
	protected ModelEvent() {}

	/**
	 * Fired while the {@link ModelManager} is reloading models, after the model registry is set up, but before it's
	 * passed to the {@link BlockModelShaper} for caching.
	 *
	 * <p>
	 * This event is fired from a worker thread and it is therefore not safe to access anything outside the
	 * model registry and {@link ModelBakery} provided in this event.<br>
	 * The {@link ModelManager} firing this event is not fully set up with the latest data when this event fires and
	 * must therefore not be accessed in this event.
	 * </p>
	 *
	 * <p>Prefer modification events under {@link ModelLoadingPlugin.Context} where possible.</p>
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class ModifyBakingResult extends ModelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onModifyBakingResult(event);
			}
		});

		public interface Callback {
			void onModifyBakingResult(ModifyBakingResult event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onModifyBakingResult(this);
		}

		private final Map<ModelResourceLocation, BakedModel> models;
		private final Function<Material, TextureAtlasSprite> textureGetter;
		private final ModelBakery modelBakery;

		@ApiStatus.Internal
		public ModifyBakingResult(Map<ModelResourceLocation, BakedModel> models, Function<Material, TextureAtlasSprite> textureGetter, ModelBakery modelBakery) {
			this.models = models;
			this.textureGetter = textureGetter;
			this.modelBakery = modelBakery;
		}

		/**
		 * @return the modifiable registry map of models and their model names
		 */
		public Map<ModelResourceLocation, BakedModel> getModels() {
			return models;
		}

		/**
		 * Returns a lookup function to retrieve {@link TextureAtlasSprite}s by name from any of the atlases handled by
		 * the {@link ModelManager}. See {@link ModelManager#VANILLA_ATLASES} for the atlases accessible through the
		 * returned function
		 *
		 * @return a function to lookup sprites from an atlas by name
		 */
		public Function<Material, TextureAtlasSprite> getTextureGetter() {
			return textureGetter;
		}

		/**
		 * @return the model loader
		 */
		public ModelBakery getModelBakery() {
			return modelBakery;
		}
	}

	/**
	 * Fired when the {@link ModelManager} is notified of the resource manager reloading.
	 * Called after the model registry is set up and cached in the {@link BlockModelShaper}.<br>
	 * The model registry given by this event is unmodifiable. To modify the model registry, use
	 * {@link ModelEvent.ModifyBakingResult} instead.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class BakingCompleted extends ModelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onBakingCompleted(event);
			}
		});

		public interface Callback {
			void onBakingCompleted(BakingCompleted event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onBakingCompleted(this);
		}

		private final ModelManager modelManager;
		private final Map<ModelResourceLocation, BakedModel> models;
		private final ModelBakery modelBakery;

		@ApiStatus.Internal
		public BakingCompleted(ModelManager modelManager, Map<ModelResourceLocation, BakedModel> models, ModelBakery modelBakery) {
			this.modelManager = modelManager;
			this.models = models;
			this.modelBakery = modelBakery;
		}

		/**
		 * @return the model manager
		 */
		public ModelManager getModelManager() {
			return modelManager;
		}

		/**
		 * @return an unmodifiable view of the registry map of models and their model names
		 */
		public Map<ModelResourceLocation, BakedModel> getModels() {
			return models;
		}

		/**
		 * @return the model loader
		 */
		public ModelBakery getModelBakery() {
			return modelBakery;
		}
	}

	/**
	 * Fired when the {@link ModelBakery} is notified of the resource manager reloading.
	 * Allows developers to register models to be loaded, along with their dependencies. Models registered through this
	 * event must use the {@link PortingLibModels#STANDALONE_VARIANT} variant.
	 * <p>Prefer {@link ModelLoadingPlugin.Context#addModels(ResourceLocation...)} where possible.</p>
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class RegisterAdditional extends ModelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onRegisterAdditional(event);
			}
		});

		public interface Callback {
			void onRegisterAdditional(RegisterAdditional event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onRegisterAdditional(this);
		}

		private final Set<ModelResourceLocation> models;

		@ApiStatus.Internal
		public RegisterAdditional(Set<ModelResourceLocation> models) {
			this.models = models;
		}

		/**
		 * Registers a model to be loaded, along with its dependencies.
		 * <p>
		 * The {@link ModelResourceLocation} passed to this method must later be used to recover the loaded model.
		 */
		public void register(ModelResourceLocation model) {
			Preconditions.checkArgument(
					model.getVariant().equals(PortingLibModels.STANDALONE_VARIANT),
					"Side-loaded models must use the '" + PortingLibModels.STANDALONE_VARIANT + "' variant");
			models.add(model);
		}
	}
}

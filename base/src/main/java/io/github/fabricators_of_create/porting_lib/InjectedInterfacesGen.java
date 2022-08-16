package io.github.fabricators_of_create.porting_lib;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;

import io.github.fabricators_of_create.porting_lib.extensions.AbstractMinecartExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.AbstractTextureExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BaseRailBlockExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BlockExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BlockItemExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.BlockModelExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.CameraExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ClientLevelExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.EntityCollisionContextExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializableCompound;
import io.github.fabricators_of_create.porting_lib.util.IPlantable;
import io.github.fabricators_of_create.porting_lib.extensions.IShearable;
import io.github.fabricators_of_create.porting_lib.extensions.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LanguageInfoExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LevelReaderExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.MapDecorationExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix3fExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.Matrix4fExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.MobEffectExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.MobEffectInstanceExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ModelStateExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ParticleExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.RenderTargetExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.ResourceLocationExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.SlotExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.StructureProcessorExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.StructureTemplateExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.TagAppenderExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.TextureAtlasSpriteExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.TierExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.Vector3fExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.VertexFormatExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

/**
 * Utility class to generate interface injection entries for Porting Lib.
 * Client only to ensure access to all target classes.
 * To enable, add '-Dporting_lib.gen_injected_interfaces=true' as a property.
 * To generate the generator, use '-Dporting_lib.gen_gen_injected_interfaces=true' as well.
 */
@Environment(EnvType.CLIENT)
public class InjectedInterfacesGen {
	public static final String PROPERTY_FLAG = "porting_lib.gen_injected_interfaces";
	public static final String IT_GOES_DEEPER = "porting_lib.gen_gen_injected_interfaces";
	public static void run() {
		boolean enabled = Boolean.parseBoolean(System.getProperty(PROPERTY_FLAG));
		if (!enabled) {
			return;
		}
		boolean inception = Boolean.parseBoolean(System.getProperty(IT_GOES_DEEPER));
		if (inception) {
			InjectedInterfacesGenGen.run();
			PortingConstants.LOGGER.info("Porting Lib successfully generated the generator for injected interfaces. my head hurts.");
			System.exit(0);
			return;
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject root = new JsonObject();
		JsonObject entries = new JsonObject();
		root.add("loom:injected_interfaces", entries);
		Builder builder = new Builder();
		buildInjections(builder);
		builder.build().forEach((target, injections) -> {
			JsonArray injectionsJson = new JsonArray();
			for (String injection : injections) {
				injectionsJson.add(new JsonPrimitive(injection.replaceAll("\\.", "/")));
			}
			entries.add(target.replaceAll("\\.", "/"), injectionsJson);
		});
		PortingConstants.LOGGER.info("\n{}", gson.toJson(root));
		PortingConstants.LOGGER.info("Porting Lib successfully generated injected interfaces.");
		System.exit(0);
	}

	public static void buildInjections(Builder builder) {
		// This section is auto-generated by InjctedInterfacesGenGen.

		// Extensions
		builder.inject(AbstractMinecart.class, AbstractMinecartExtensions.class);
		builder.inject(AbstractTexture.class, AbstractTextureExtensions.class);
		builder.inject(BaseRailBlock.class, BaseRailBlockExtensions.class);
		builder.inject(BlockEntity.class, BlockEntityExtensions.class);
		builder.inject(Block.class, BlockExtensions.class);
		builder.inject(BlockItem.class, BlockItemExtensions.class);
		builder.inject(BlockModel.class, BlockModelExtensions.class);
		builder.inject(Camera.class, CameraExtensions.class);
		builder.inject(ClientLevel.class, ClientLevelExtensions.class);
		builder.inject(EntityCollisionContext.class, EntityCollisionContextExtensions.class);
		builder.inject(Entity.class, EntityExtensions.class);
		builder.injectReversed(INBTSerializableCompound.class, Entity.class, BlockEntity.class, ItemStack.class);
		builder.injectReversed(IPlantable.class, BambooBlock.class, BushBlock.class, CactusBlock.class, StemBlock.class, SugarCaneBlock.class);
		builder.injectReversed(IShearable.class, DeadBushBlock.class, LeavesBlock.class, MushroomCow.class, SeagrassBlock.class, Sheep.class, SnowGolem.class, TallGrassBlock.class, VineBlock.class, WebBlock.class);
		// note: ITeleporter has no impl
		builder.inject(Item.class, ItemExtensions.class);
		builder.inject(ItemStack.class, ItemStackExtensions.class);
		builder.inject(LanguageInfo.class, LanguageInfoExtensions.class);
		builder.inject(Level.class, LevelExtensions.class);
		builder.inject(LevelReader.class, LevelReaderExtensions.class);
		builder.inject(MapDecoration.class, MapDecorationExtensions.class);
		builder.inject(Matrix3f.class, Matrix3fExtensions.class);
		builder.inject(Matrix4f.class, Matrix4fExtensions.class);
		builder.inject(MobEffect.class, MobEffectExtensions.class);
		builder.inject(MobEffectInstance.class, MobEffectInstanceExtensions.class);
		builder.inject(ModelState.class, ModelStateExtensions.class);
		builder.inject(Particle.class, ParticleExtensions.class);
		builder.inject(RenderTarget.class, RenderTargetExtensions.class);
		builder.inject(ResourceLocation.class, ResourceLocationExtensions.class);
		builder.inject(Slot.class, SlotExtensions.class);
		builder.inject(StructureProcessor.class, StructureProcessorExtensions.class);
		builder.inject(StructureTemplate.class, StructureTemplateExtensions.class);
		builder.inject(TagAppender.class, TagAppenderExtensions.class);
		builder.inject(TextureAtlasSprite.class, TextureAtlasSpriteExtensions.class);
		builder.inject(Tier.class, TierExtensions.class);
		builder.inject(Transformation.class, TransformationExtensions.class);
		builder.inject(Vector3f.class, Vector3fExtensions.class);
		builder.inject(VertexFormat.class, VertexFormatExtensions.class);

		// aaaaaaaaaaaaaaaaaaaaaaaaaaaa
		// we can't do this since then subclasses of targets need to implement accessors
		// I have an idea but I don't have time to do it yet
		// Common accessors
//		builder.inject(AbstractMinecart.class, AbstractMinecartAccessor.class);
//		builder.inject(AbstractProjectileDispenseBehavior.class, AbstractProjectileDispenseBehaviorAccessor.class);
//		builder.inject(ArrayVoxelShape.class, ArrayVoxelShapeAccessor.class);
//		builder.inject(AxeItem.class, AxeItemAccessor.class);
//		builder.inject(BaseSpawner.class, BaseSpawnerAccessor.class);
//		builder.inject(BeaconBlockEntity.class, BeaconBlockEntityAccessor.class);
//		builder.inject(BiomeManager.class, BiomeManagerAccessor.class);
//		builder.inject(Block.class, BlockAccessor.class);
//		builder.inject(BlockEntity.class, BlockEntityAccessor.class);
//		builder.inject(BucketItem.class, BucketItemAccessor.class);
//		builder.inject(BundleItem.class, BundleItemAccessor.class);
//		builder.inject(ClientboundPlayerAbilitiesPacket.class, ClientboundPlayerAbilitiesPacketAccessor.class);
//		builder.inject(CubeVoxelShape.class, CubeVoxelShapeAccessor.class);
//		builder.inject(DamageSource.class, DamageSourceAccessor.class);
//		builder.inject(Entity.class, EntityAccessor.class);
//		builder.inject(HashMapPalette.class, HashMapPaletteAccessor.class);
//		builder.inject(HumanoidModel.class, HumanoidModelAccessor.class);
//		builder.inject(Item.class, ItemAccessor.class);
//		builder.inject(ItemValue.class, ItemValueAccessor.class);
//		builder.inject(LiquidBlock.class, LiquidBlockAccessor.class);
//		builder.inject(LivingEntity.class, LivingEntityAccessor.class);
//		builder.inject(MinecraftServer.class, MinecraftServerAccessor.class);
//		builder.inject(Mob.class, MobAccessor.class);
//		builder.inject(Painting.class, PaintingAccessor.class);
//		builder.inject(Player.class, PlayerAccessor.class);
//		builder.inject(PotionBrewing.Mix.class, PotionBrewing$MixAccessor.class);
//		builder.inject(PotionBrewing.class, PotionBrewingAccessor.class);
//		builder.inject(RailState.class, RailStateAccessor.class);
//		builder.inject(RecipeManager.class, RecipeManagerAccessor.class);
//		builder.inject(ServerGamePacketListenerImpl.class, ServerGamePacketListenerImplAccessor.class);
//		builder.inject(Slot.class, SlotAccessor.class);
//		builder.inject(StairBlock.class, StairBlockAccessor.class);
//		builder.inject(StructureTemplate.class, StructureTemplateAccessor.class);
//		builder.inject(TagValue.class, TagValueAccessor.class);
//
//		// Client accessors
//		builder.inject(AbstractContainerScreen.class, AbstractContainerScreenAccessor.class);
//		builder.inject(AbstractSelectionList.Entry.class, AbstractSelectionList$EntryAccessor.class);
//		builder.inject(AbstractSelectionList.class, AbstractSelectionListAccessor.class);
//		builder.inject(AbstractWidget.class, AbstractWidgetAccessor.class);
//		builder.inject(AgeableListModel.class, AgeableListModelAccessor.class);
//		builder.inject(BlockModel.Deserializer.class, BlockModel$DeserializerAccessor.class);
//		builder.inject(BlockModel.class, BlockModelAccessor.class);
//		builder.inject(BlockRenderDispatcher.class, BlockRenderDispatcherAccessor.class);
//		builder.inject(ClientPacketListener.class, ClientPacketListenerAccessor.class);
//		builder.inject(Font.class, FontAccessor.class);
//		builder.inject(GameRenderer.class, GameRendererAccessor.class);
//		builder.inject(ItemInHandRenderer.class, ItemInHandRendererAccessor.class);
//		builder.inject(ItemRenderer.class, ItemRendererAccessor.class);
//		builder.inject(KeyMapping.class, KeyMappingAccessor.class);
//		builder.inject(Minecraft.class, MinecraftAccessor.class);
//		builder.inject(ModelBakery.class, ModelBakeryAccessor.class);
//		builder.inject(ModelPart.class, ModelPartAccessor.class);
//		builder.inject(Particle.class, ParticleAccessor.class);
//		builder.inject(ParticleEngine.class, ParticleEngineAccessor.class);
//		builder.inject(RenderType.class, RenderTypeAccessor.class);
//		builder.inject(Screen.class, ScreenAccessor.class);
//		builder.inject(Sheets.class, SheetsAccessor.class);
//		builder.inject(SimpleBakedModel.Builder.class, SimpleBakedModel$BuilderAccessor.class);
//		builder.inject(TextureAtlasSprite.AnimatedTexture.class, TextureAtlasSprite$AnimatedTextureAccessor.class);
//		builder.inject(TextureSheetParticle.class, TextureSheetParticleAccessor.class);
//		builder.inject(TitleScreen.class, TitleScreenAccessor.class);
	}

	public static class Builder {
		public final Multimap<Class<?>, Class<?>> entries = HashMultimap.create();

		public void inject(Class<?> target, Class<?>... injections) {
			entries.putAll(target, Arrays.asList(injections));
		}

		public void injectReversed(Class<?> injection, Class<?>... targets) {
			for (Class<?> c : targets) {
				inject(c, injection);
			}
		}

		public Map<String, String[]> build() {
			Map<String, String[]> output = new HashMap<>(entries.size());
			MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();
			entries.asMap().forEach((target, injections) -> {
				String targetName = resolver.unmapClassName("intermediary", target.getName());
				String[] injectionNames = injections.stream().map(Class::getName).toArray(String[]::new);
				output.put(targetName, injectionNames);
			});
			return output;
		}
	}

	// inception
	public static class InjectedInterfacesGenGen {
		public static void run() {
			Path run = FabricLoader.getInstance().getGameDir();
			Path root = run.getParent();
			Path portingLib = root
					.resolve("src")
					.resolve("main")
					.resolve("java")
					.resolve("io")
					.resolve("github")
					.resolve("fabricators_of_create")
					.resolve("porting_lib");
			File extensionsDir = portingLib.resolve("extensions").toFile();
			StringBuilder code = new StringBuilder()
					.append("\n\t\t// This section is auto-generated by InjectedInterfacesGenGen.\n")
					.append("\t\t// If any extensions or accessors target multiple classes, they will not be correct. After verifying\n")
					.append("\t\t// correctness and filling in failures manually, please remove lines 2 and 3 of this comment.\n\n")
					.append("\t\t// Extensions\n");
			for (File extension : extensionsDir.listFiles()) {
				String name = extension.getName();
				name = name.substring(0, name.lastIndexOf(".java"));
				if (!name.contains("Extensions")) {
					code.append("\t\t// Could not not generate generator for extension: ").append(name).append('\n');
					continue;
				}
				prefix(code);
				String target = name.substring(0, name.lastIndexOf("Extensions")).replaceAll("\\$", ".");
				code.append(target).append(".class");
				separate(code);
				code.append(name).append(".class");
				end(code);
			}
			// see comment above
//			File commonAccessors = portingLib
//					.resolve("mixin")
//					.resolve("common")
//					.resolve("accessor").toFile();
//			code.append("\n\t\t// Common accessors\n");
//			for (File extension : commonAccessors.listFiles()) {
//				String name = extension.getName();
//				name = name.substring(0, name.lastIndexOf(".java"));
//				prefix(code);
//				String target = name.substring(0, name.lastIndexOf("Accessor")).replaceAll("\\$", ".");
//				code.append(target).append(".class");
//				separate(code);
//				code.append(name).append(".class");
//				end(code);
//			}
//
//			File clientAccessors = portingLib
//					.resolve("mixin")
//					.resolve("client")
//					.resolve("accessor").toFile();
//			code.append("\n\t\t// Client accessors\n");
//			for (File extension : clientAccessors.listFiles()) {
//				String name = extension.getName();
//				name = name.substring(0, name.lastIndexOf(".java"));
//				prefix(code);
//				String target = name.substring(0, name.lastIndexOf("Accessor")).replaceAll("\\$", ".");
//				code.append(target).append(".class");
//				separate(code);
//				code.append(name).append(".class");
//				end(code);
//			}

			PortingConstants.LOGGER.info(code.toString());
		}
	}

	public static void prefix(StringBuilder builder) {
		builder.append("\t\tbuilder.inject(");
	}

	public static void separate(StringBuilder builder) {
		builder.append(", ");
	}

	public static void end(StringBuilder builder) {
		builder.append(");\n");
	}
}

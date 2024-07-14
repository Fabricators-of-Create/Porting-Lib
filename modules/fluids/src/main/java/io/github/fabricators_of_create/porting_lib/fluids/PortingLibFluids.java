package io.github.fabricators_of_create.porting_lib.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class PortingLibFluids implements ModInitializer {
	public static final Codec<Long> POSITIVE_LONG = longRangeWithMessage(1, Long.MAX_VALUE, (integer) -> {
		return "Value must be positive: " + integer;
	});
	public static final ResourceKey<Registry<FluidType>> FLUID_TYPE_REGISTRY = ResourceKey.createRegistryKey(PortingLib.id("fluid_type"));
	public static final Registry<FluidType> FLUID_TYPES = FabricRegistryBuilder.createDefaulted(FLUID_TYPE_REGISTRY, PortingLib.id("empty")).buildAndRegister();

	public static final FluidType EMPTY_TYPE =
			new FluidType(FluidType.Properties.create()
					.descriptionId("block.minecraft.air")
					.motionScale(1D)
					.canPushEntity(false)
					.canSwim(false)
					.canDrown(false)
					.fallDistanceModifier(1F)
					.pathType(null)
					.adjacentPathType(null)
					.density(0)
					.temperature(0)
					.viscosity(0))
			{
				@Override
				public void setItemMovement(ItemEntity entity) {
					if (!entity.isNoGravity()) entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
				}
			};
	public static final FluidType WATER_TYPE =
			new FluidType(FluidType.Properties.create()
					.descriptionId("block.minecraft.water")
					.fallDistanceModifier(0F)
					.canExtinguish(true)
					.canConvertToSource(true)
					.supportsBoating(true)
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
					.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
					.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
					.canHydrate(true))
			{
				@Override
				public @Nullable PathType getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog)
				{
					return canFluidLog ? super.getBlockPathType(state, level, pos, mob, true) : null;
				}

//				@Override
//				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
//				{
//					consumer.accept(new IClientFluidTypeExtensions()
//					{
//						private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png"),
//								WATER_STILL = new ResourceLocation("block/water_still"),
//								WATER_FLOW = new ResourceLocation("block/water_flow"),
//								WATER_OVERLAY = new ResourceLocation("block/water_overlay");
//
//						@Override
//						public ResourceLocation getStillTexture()
//						{
//							return WATER_STILL;
//						}
//
//						@Override
//						public ResourceLocation getFlowingTexture()
//						{
//							return WATER_FLOW;
//						}
//
//						@Nullable
//						@Override
//						public ResourceLocation getOverlayTexture()
//						{
//							return WATER_OVERLAY;
//						}
//
//						@Override
//						public ResourceLocation getRenderOverlayTexture(Minecraft mc)
//						{
//							return UNDERWATER_LOCATION;
//						}
//
//						@Override
//						public int getTintColor()
//						{
//							return 0xFF3F76E4;
//						}
//
//						@Override
//						public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos)
//						{
//							return BiomeColors.getAverageWaterColor(getter, pos) | 0xFF000000;
//						}
//					});
//				}
			};
	public static final FluidType LAVA_TYPE =
			new FluidType(FluidType.Properties.create()
					.descriptionId("block.minecraft.lava")
					.canSwim(false)
					.canDrown(false)
					.pathType(PathType.LAVA)
					.adjacentPathType(null)
					.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
					.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
					.lightLevel(15)
					.density(3000)
					.viscosity(6000)
					.temperature(1300))
			{
				@Override
				public double motionScale(Entity entity)
				{
					return entity.level().dimensionType().ultraWarm() ? 0.007D : 0.0023333333333333335D;
				}

				@Override
				public void setItemMovement(ItemEntity entity)
				{
					Vec3 vec3 = entity.getDeltaMovement();
					entity.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
				}

//				@Override
//				public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer)
//				{
//					consumer.accept(new IClientFluidTypeExtensions()
//					{
//						private static final ResourceLocation LAVA_STILL = new ResourceLocation("block/lava_still"),
//								LAVA_FLOW = new ResourceLocation("block/lava_flow");
//
//						@Override
//						public ResourceLocation getStillTexture()
//						{
//							return LAVA_STILL;
//						}
//
//						@Override
//						public ResourceLocation getFlowingTexture()
//						{
//							return LAVA_FLOW;
//						}
//					});
//				}
			};

	@Override
	public void onInitialize() {
		Registry.register(FLUID_TYPES, PortingLib.id("empty"), EMPTY_TYPE);
		Registry.register(FLUID_TYPES, PortingLib.id("water"), WATER_TYPE);
		Registry.register(FLUID_TYPES, PortingLib.id("lava"), LAVA_TYPE);
	}

	private static Codec<Long> longRangeWithMessage(long min, long max, Function<Long, String> messageFunction) {
		return Codec.LONG.validate((value) -> {
			return value.compareTo(min) >= 0 && value.compareTo(max) <= 0 ? DataResult.success(value) : DataResult.error(() -> {
				return messageFunction.apply(value);
			});
		});
	}
}

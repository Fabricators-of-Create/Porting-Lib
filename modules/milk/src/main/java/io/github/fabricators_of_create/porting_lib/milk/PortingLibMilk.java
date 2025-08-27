package io.github.fabricators_of_create.porting_lib.milk;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.fluids.BaseFlowingFluid;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import io.github.fabricators_of_create.porting_lib.milk.client.PortingLibMilkClient;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.mixin.transfer.BucketItemAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.material.Fluid;

public class PortingLibMilk {
	private static boolean enableMilkFluid = false;

	public static final DeferredHolder<SoundEvent, SoundEvent> BUCKET_EMPTY_MILK = DeferredHolder.create(Registries.SOUND_EVENT, PortingLib.id("item.bucket.empty_milk"));
	public static final DeferredHolder<SoundEvent, SoundEvent> BUCKET_FILL_MILK = DeferredHolder.create(Registries.SOUND_EVENT, PortingLib.id("item.bucket.fill_milk"));
	public static final DeferredHolder<FluidType, FluidType> MILK_TYPE = DeferredHolder.create(PortingLibFluids.FLUID_TYPE_REGISTRY, PortingLib.id("milk"));
	public static final DeferredHolder<Fluid, Fluid> MILK = DeferredHolder.create(Registries.FLUID, PortingLib.id("milk"));
	public static final DeferredHolder<Fluid, Fluid> FLOWING_MILK = DeferredHolder.create(Registries.FLUID, PortingLib.id("flowing_milk"));

	/**
	 * Run this method during mod constructor to enable milk and add it to the Minecraft milk bucket
	 */
	public static void enableMilkFluid() {
		if (!enableMilkFluid) {
			// register milk fill, empty sounds (delegates to water fill, empty sounds)
			Registry.register(BuiltInRegistries.SOUND_EVENT, BUCKET_EMPTY_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_EMPTY_MILK.getId()));
			Registry.register(BuiltInRegistries.SOUND_EVENT, BUCKET_FILL_MILK.getId(), SoundEvent.createVariableRangeEvent(BUCKET_FILL_MILK.getId()));

			// register fluid type
			Registry.register(PortingLibFluids.FLUID_TYPES, MILK_TYPE.unwrapKey().orElseThrow(), new FluidType(
					FluidType.Properties.create().density(1024).viscosity(1024)
							.sound(SoundActions.BUCKET_FILL, BUCKET_FILL_MILK.value())
							.sound(SoundActions.BUCKET_EMPTY, BUCKET_EMPTY_MILK.value()))
			);

			// register fluids
			// set up properties
			BaseFlowingFluid.Properties properties = new BaseFlowingFluid.Properties(MILK_TYPE::value, MILK::value, FLOWING_MILK::value).bucket(() -> Items.MILK_BUCKET);
			Registry.register(BuiltInRegistries.FLUID, MILK.getId(), new BaseFlowingFluid.Source(properties));
			Registry.register(BuiltInRegistries.FLUID, FLOWING_MILK.getId(), new BaseFlowingFluid.Flowing(properties));

			FluidStorage.GENERAL_COMBINED_PROVIDER.register(context -> {
				if (context.getItemVariant().getItem() instanceof MilkBucketItem bucketItem) {
					Fluid bucketFluid = MILK.get();

					// Make sure the mapping is bidirectional.
					if (bucketFluid != null && bucketFluid.getBucket() == bucketItem) {
						return new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(bucketFluid), FluidConstants.BUCKET);
					}
				}

				return null;
			});

			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				PortingLibMilkClient.init();
			}
		}
		enableMilkFluid = true;
	}
}

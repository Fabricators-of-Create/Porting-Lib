package io.github.fabricators_of_create.porting_lib.models.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.CustomLoaderBuilder;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.world.level.material.Fluid;

public class DynamicFluidContainerModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
	public static <T extends ModelBuilder<T>> DynamicFluidContainerModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
		return new DynamicFluidContainerModelBuilder<>(parent, existingFileHelper);
	}

	private FluidVariant fluid;
	private Boolean flipGas;
	private Boolean applyTint;
	private Boolean coverIsMask;
	private Boolean applyFluidLuminosity;

	protected DynamicFluidContainerModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(PortingLib.id("fluid_container"), parent, existingFileHelper, false);
	}

	public DynamicFluidContainerModelBuilder<T> fluid(FluidVariant fluid) {
		Preconditions.checkNotNull(fluid, "fluid must not be null");
		this.fluid = fluid;
		return this;
	}

	public DynamicFluidContainerModelBuilder<T> fluid(Fluid fluid) {
		Preconditions.checkNotNull(fluid, "fluid must not be null");
		this.fluid = FluidVariant.of(fluid);
		return this;
	}

	public DynamicFluidContainerModelBuilder<T> flipGas(boolean flip) {
		this.flipGas = flip;
		return this;
	}

	public DynamicFluidContainerModelBuilder<T> applyTint(boolean tint) {
		this.applyTint = tint;
		return this;
	}

	public DynamicFluidContainerModelBuilder<T> coverIsMask(boolean coverIsMask) {
		this.coverIsMask = coverIsMask;
		return this;
	}

	public DynamicFluidContainerModelBuilder<T> applyFluidLuminosity(boolean applyFluidLuminosity) {
		this.applyFluidLuminosity = applyFluidLuminosity;
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);

		Preconditions.checkNotNull(fluid, "fluid must not be null");

		json.add("variant", FluidVariant.CODEC.encodeStart(JsonOps.INSTANCE, fluid).getOrThrow());

		if (flipGas != null)
			json.addProperty("flip_gas", flipGas);

		if (applyTint != null)
			json.addProperty("apply_tint", applyTint);

		if (coverIsMask != null)
			json.addProperty("cover_is_mask", coverIsMask);

		if (applyFluidLuminosity != null)
			json.addProperty("apply_fluid_luminosity", applyFluidLuminosity);

		return json;
	}
}

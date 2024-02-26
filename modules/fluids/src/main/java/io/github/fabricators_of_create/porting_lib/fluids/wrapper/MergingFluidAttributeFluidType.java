package io.github.fabricators_of_create.porting_lib.fluids.wrapper;

import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundAction;
import io.github.fabricators_of_create.porting_lib.fluids.sound.SoundActions;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class MergingFluidAttributeFluidType extends FluidType {
	private final FluidVariant variant;
	private final FluidVariantAttributeHandler handler;
	private final FluidType superType;
	public MergingFluidAttributeFluidType(FluidType superType, FluidVariant variant, FluidVariantAttributeHandler handler) {
		super(Properties.create()
				.viscosity(handler.getViscosity(variant, null))
				.temperature(handler.getTemperature(variant))
				.lightLevel(handler.getLuminance(variant))
				.sound(SoundActions.BUCKET_FILL, handler.getFillSound(variant).get())
				.sound(SoundActions.BUCKET_EMPTY, handler.getEmptySound(variant).get())
				.density(handler.isLighterThanAir(variant) ? -1 : 1)
		);
		this.variant = variant;
		this.handler = handler;
		this.superType = superType;
	}

	@Override
	public Component getDescription() {
		return handler.getName(variant);
	}

	@Override
	public int getTemperature(FluidStack stack) {
		return handler.getTemperature(stack.getType());
	}

	@Override
	public int getViscosity(FluidStack stack) {
		return handler.getViscosity(stack.getType(), null);
	}

	@Override
	public int getViscosity(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		if (getter instanceof Level level)
			return handler.getViscosity(FluidVariant.of(state.getType()), level);
		return super.getViscosity(state, getter, pos);
	}

	@Override
	public int getLightLevel(FluidStack stack) {
		return handler.getLuminance(stack.getType());
	}

	@Override
	public int getDensity() {
		return superType.getDensity();
	}

	@Override
	public int getTemperature() {
		return superType.getTemperature();
	}

	@Override
	public int getViscosity() {
		return superType.getViscosity();
	}

	@Override
	public Rarity getRarity() {
		return superType.getRarity();
	}

	@Override
	public @Nullable SoundEvent getSound(SoundAction action) {
		return superType.getSound(action);
	}

	@Override
	public double motionScale(Entity entity) {
		return superType.motionScale(entity);
	}

	@Override
	public boolean canPushEntity(Entity entity) {
		return superType.canPushEntity(entity);
	}

	@Override
	public boolean canSwim(Entity entity) {
		return superType.canSwim(entity);
	}

	@Override
	public float getFallDistanceModifier(Entity entity) {
		return superType.getFallDistanceModifier(entity);
	}

	@Override
	public boolean canExtinguish(Entity entity) {
		return superType.canExtinguish(entity);
	}

	@Override
	public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
		return superType.move(state, entity, movementVector, gravity);
	}

	@Override
	public boolean canDrownIn(LivingEntity entity) {
		return superType.canDrownIn(entity);
	}

	@Override
	public void setItemMovement(ItemEntity entity) {
		superType.setItemMovement(entity);
	}

	@Override
	public boolean supportsBoating(Boat boat) {
		return superType.supportsBoating(boat);
	}

	@Override
	public boolean supportsBoating(FluidState state, Boat boat) {
		return superType.supportsBoating(state, boat);
	}

	@Override
	public boolean canRideVehicleUnder(Entity vehicle, Entity rider) {
		return superType.canRideVehicleUnder(vehicle, rider);
	}

	@Override
	public boolean canHydrate(Entity entity) {
		return superType.canHydrate(entity);
	}

	@Override
	public @Nullable SoundEvent getSound(Entity entity, SoundAction action) {
		return superType.getSound(entity, action);
	}

	@Override
	public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
		return superType.canExtinguish(state, getter, pos);
	}

	@Override
	public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
		return superType.canConvertToSource(state, reader, pos);
	}

	@Override
	public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
		return superType.getBlockPathType(state, level, pos, mob, canFluidLog);
	}

	@Override
	public @Nullable BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
		return superType.getAdjacentBlockPathType(state, level, pos, mob, originalType);
	}

	@Override
	public @Nullable SoundEvent getSound(@Nullable Player player, BlockGetter getter, BlockPos pos, SoundAction action) {
		return superType.getSound(player, getter, pos, action);
	}

	@Override
	public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos) {
		return superType.canHydrate(state, getter, pos, source, sourcePos);
	}

	@Override
	public int getLightLevel(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return superType.getLightLevel(state, getter, pos);
	}

	@Override
	public int getDensity(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return superType.getDensity(state, getter, pos);
	}

	@Override
	public int getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		return superType.getTemperature(state, getter, pos);
	}

	@Override
	public boolean canConvertToSource(FluidStack stack) {
		return superType.canConvertToSource(stack);
	}

	@Override
	public @Nullable SoundEvent getSound(FluidStack stack, SoundAction action) {
		return superType.getSound(stack, action);
	}

	@Override
	public boolean canHydrate(FluidStack stack) {
		return superType.canHydrate(stack);
	}

	@Override
	public int getDensity(FluidStack stack) {
		return superType.getDensity(stack);
	}

	@Override
	public Rarity getRarity(FluidStack stack) {
		return superType.getRarity(stack);
	}

	@Override
	public ItemStack getBucket(FluidStack stack) {
		return superType.getBucket(stack);
	}

	@Override
	public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
		return superType.getBlockForFluidState(getter, pos, state);
	}

	@Override
	public FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack) {
		return superType.getStateForPlacement(getter, pos, stack);
	}

	@Override
	public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
		return superType.isVaporizedOnPlacement(level, pos, stack);
	}

	@Override
	public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
		superType.onVaporize(player, level, pos, stack);
	}

	public FluidType getRegisteredWrapper() {
		return this.superType;
	}
}

package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import io.github.fabricators_of_create.porting_lib.extensions.BlockItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

@SuppressWarnings({"removal", "UnstableApiUsage"})
public class PortingHooks {
	public static int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
		// Logic from tryHarvestBlock for pre-canceling the event
		boolean preCancelEvent = false;
		ItemStack itemstack = entityPlayer.getMainHandItem();
		if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(world.getBlockState(pos), world, pos, entityPlayer)) {
			preCancelEvent = true;
		}

		if (gameType.isBlockPlacingRestricted()) {
			if (gameType == GameType.SPECTATOR)
				preCancelEvent = true;

			if (!entityPlayer.mayBuild()) {
				if (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(world, pos, false)))
					preCancelEvent = true;
			}
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, world.getFluidState(pos).createLegacyBlock()));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvents.BreakEvent event = new BlockEvents.BreakEvent(world, pos, state, entityPlayer);
		event.setCanceled(preCancelEvent);
		event.sendEvent();

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));

			// Update any tile entity data for this block
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity != null) {
				Packet<?> pkt = blockEntity.getUpdatePacket();
				if (pkt != null) {
					entityPlayer.connection.send(pkt);
				}
			}
		}
		return event.isCanceled() ? -1 : event.getExpToDrop();
	}

	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().put(part.getId(), part);
					}
				}
			}
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if(entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().remove(part.getId());
					}
				}
			}
		});
		EntityEvents.ON_JOIN_WORLD.register((entity, world, loadedFromDisk) -> {
			if (entity.getClass().equals(ItemEntity.class)) {
				ItemStack stack = ((ItemEntity)entity).getItem();
				Item item = stack.getItem();
				if (item.hasCustomEntity(stack)) {
					Entity newEntity = item.createEntity(world, entity, stack);
					if (newEntity != null) {
						entity.discard();
						var executor = LogicalSidedProvider.WORKQUEUE.get(world.isClientSide ? EnvType.CLIENT : EnvType.SERVER);
						executor.tell(new TickTask(0, () -> world.addFreshEntity(newEntity)));
						return false;
					}
				}
			}
			return true;
		});
		RegistryEntryRemovedCallback.event(Registry.ITEM).register((rawId, id, item) -> {
			if (item instanceof BlockItemExtensions blockItem) {
				blockItem.removeFromBlockToItemMap(Item.BY_BLOCK, item);
			}
		});
		ModsLoadedCallback.EVENT.register(envType -> {
			Registry.FLUID.forEach(fluid -> {
				if (envType == EnvType.SERVER && FluidVariantAttributes.getHandler(fluid) == null)
					FluidVariantAttributes.register(fluid, new FluidVariantFluidAttributesHandler(fluid.getAttributes()));
			});
		});
	}

	public static FluidAttributes getFluidAttributesFromVariant(Fluid fluid) {
		if (!fluid.isSource(fluid.defaultFluidState()) && fluid instanceof FlowingFluid flowingFluid)
			return flowingFluid.getSource().getAttributes();
		if (!fluid.isSource(fluid.defaultFluidState()) && fluid != Fluids.EMPTY)
			return Fluids.WATER.getAttributes(); // Return waters attributes if all else fails
		FluidVariantAttributeHandler handler = FluidVariantAttributes.getHandler(fluid);
		if (handler == null)
			handler = FluidVariantAttributes.getHandler(Fluids.WATER); // Default to water
		FluidVariant variant = FluidVariant.of(fluid);
		ResourceLocation stillTexture = null;
		ResourceLocation flowingTexture = null;
		ResourceLocation overlayTexture = null;
		int color = -1;
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			TextureAtlasSprite[] sprites = FluidVariantRendering.getSprites(variant);

			stillTexture = sprites[0] == null ? SimpleFluidRenderHandler.WATER_STILL : sprites[0].getName();
			flowingTexture = sprites[1] == null ? SimpleFluidRenderHandler.WATER_FLOWING : sprites[1].getName();
			if (sprites.length == 3)
				overlayTexture = sprites[2] == null ? SimpleFluidRenderHandler.WATER_OVERLAY : sprites[2].getName();
			color = FluidVariantRendering.getColor(variant);
		}
		FluidAttributes.Builder builder = FluidAttributes.builder(stillTexture, flowingTexture);
		builder.sound(handler.getFillSound(variant).orElse(null), handler.getEmptySound(variant).orElse(null));
		builder.overlay(overlayTexture);
		builder.viscosity(handler.getViscosity(variant, null));
		builder.luminosity(handler.getLuminance(variant));
		builder.temperature(handler.getTemperature(variant));
		if (handler.isLighterThanAir(variant))
			builder.gaseous();
		builder.color(color);
		return builder.build(fluid);
	}

	public static int getLootingLevel(Entity target, @Nullable Entity killer, @Nullable DamageSource cause) {
		if (killer instanceof LivingEntity)
			return EnchantmentHelper.getMobLooting((LivingEntity)killer);
		return 0;
	}
}

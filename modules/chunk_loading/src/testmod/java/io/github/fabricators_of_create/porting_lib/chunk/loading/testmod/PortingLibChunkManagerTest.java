package io.github.fabricators_of_create.porting_lib.chunk.loading.testmod;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import io.github.fabricators_of_create.porting_lib.chunk.loading.TicketController;
import io.github.fabricators_of_create.porting_lib.chunk.loading.TicketSet;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class PortingLibChunkManagerTest implements ModInitializer {
	public static final String MODID = "forge_chunk_manager_test";
	private static final Logger LOGGER = LogManager.getLogger(MODID);
	private static final Block CHUNK_LOADER_BLOCK = registerBlock("chunk_loader", ChunkLoaderBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
	private static final BlockItem CHUNK_LOADER_ITEM = registerBlockItem(CHUNK_LOADER_BLOCK);
	private static final TicketController CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(MODID, "default"), (world, ticketHelper) -> {
		for (Map.Entry<BlockPos, TicketSet> entry : ticketHelper.getBlockTickets().entrySet()) {
			BlockPos key = entry.getKey();
			int ticketCount = entry.getValue().normal().size();
			int naturalSpawningTicketCount = entry.getValue().naturalSpawning().size();
			if (world.getBlockState(key).is(CHUNK_LOADER_BLOCK))
				LOGGER.info("Allowing {} chunk tickets and {} forced natural spawning chunk tickets to be reinstated for position: {}.", ticketCount, naturalSpawningTicketCount, key);
			else {
				ticketHelper.removeAllTickets(key);
				LOGGER.info("Removing {} chunk tickets and {} forced natural spawning chunk tickets for no longer valid position: {}.", ticketCount, naturalSpawningTicketCount, key);
			}
		}
		for (Map.Entry<UUID, TicketSet> entry : ticketHelper.getEntityTickets().entrySet()) {
			UUID key = entry.getKey();
			int ticketCount = entry.getValue().normal().size();
			int naturalSpawningTicketCount = entry.getValue().naturalSpawning().size();
			LOGGER.info("Allowing {} chunk tickets and {} forced natural spawning chunk tickets to be reinstated for entity: {}.", ticketCount, naturalSpawningTicketCount, key);
		}
	});

	public static <B extends Block> B registerBlock(String id, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties properties) {
		return Registry.register(BuiltInRegistries.BLOCK, PortingLib.id(id), func.apply(properties.setId(ResourceKey.create(Registries.BLOCK, PortingLib.id(id)))));
	}

	public static BlockItem registerBlockItem(Block block) {
		return Registry.register(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK.getKey(block), new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, BuiltInRegistries.BLOCK.getKey(block)))));
	}

	@Override
	public void onInitialize() {
		ForcedChunkManager.registerController(CONTROLLER);
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(PortingLibChunkManagerTest::addCreative);
	}

	private static void addCreative(FabricItemGroupEntries entries) {
		entries.accept(CHUNK_LOADER_ITEM);
	}

	private static class ChunkLoaderBlock extends Block {
		public ChunkLoaderBlock(Properties properties) {
			super(properties);
		}

		@Override
		public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
			super.onPlace(state, worldIn, pos, oldState, isMoving);
			if (worldIn instanceof ServerLevel) {
				ChunkPos chunkPos = new ChunkPos(pos);
				CONTROLLER.forceChunk((ServerLevel) worldIn, pos, chunkPos.x, chunkPos.z, true, true);
			}
		}

		@Deprecated
		@Override
		public void affectNeighborsAfterRemoval(BlockState state, ServerLevel worldIn, BlockPos pos, boolean isMoving) {
			super.affectNeighborsAfterRemoval(state, worldIn, pos, isMoving);
			ChunkPos chunkPos = new ChunkPos(pos);
			//TODO: If the block is removed without neighbor updates this won't be fired, is there a more proper method for us to override?
			CONTROLLER.forceChunk(worldIn, pos, chunkPos.x, chunkPos.z, false, true);
		}
	}
}

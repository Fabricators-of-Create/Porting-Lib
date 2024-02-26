package io.github.fabricators_of_create.porting_lib.transfer.item;

//import io.github.fabricators_of_create.porting_lib.PortingLib;
//import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
//import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
//import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
//import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
//import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.NbtUtils;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
// todo. I'm tired of working on this.
//public class ShulkerBoxStorage extends SnapshotParticipant<CompoundTag> implements Storage<ItemVariant> {
//	private final ContainerItemContext context;
//	protected final Storage<ItemVariant> storage;
//	protected final ShulkerBoxBlockEntity be;
//	public final Item item;
//
//	public ShulkerBoxStorage(Item item, ContainerItemContext context) {
//		this.context = context;
//		this.item = item;
//		CompoundTag tag = context.getItemVariant().getNbt();
//		if (tag != null && !tag.isEmpty()) {
//			tag = tag.getCompound("BlockEntityTag");
//			BlockEntity be = BlockEntity.loadStatic(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState(), tag);
//			if (be instanceof ShulkerBoxBlockEntity shulker) {
//				storage = InventoryStorage.of(shulker, null);
//				this.be = shulker;
//				return;
//			}
//		}
//		PortingLib.LOGGER.error(
//				"Failed to load shulker from tag! \nItem: [{}] \nTag: [{}]",
//				context.getItemVariant(),
//				tag == null ? null : NbtUtils.prettyPrint(tag)
//		);
//		storage = Storage.empty();
//		be = null;
//	}
//
//	@Override
//	protected CompoundTag createSnapshot() {
//		return null;
//	}
//
//	@Override
//	protected void readSnapshot(CompoundTag snapshot) {
//
//	}
//}

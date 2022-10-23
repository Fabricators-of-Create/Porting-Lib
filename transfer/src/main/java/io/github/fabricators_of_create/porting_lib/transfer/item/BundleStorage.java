package io.github.fabricators_of_create.porting_lib.transfer.item;

//import io.github.fabricators_of_create.porting_lib.model_loader.mixin.common.accessor.BundleItemAccessor;
//import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
//import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
//import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.nbt.Tag;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//
//import javax.annotation.Nullable;
//
//import java.util.Optional;
// todo. I'm tired of working on this.
//public class BundleStorage extends ItemStackHandler {
//	protected final ContainerItemContext context;
//	public final Item item;
//
//	public BundleStorage(Item item, ContainerItemContext context) {
//		this.item = item;
//		this.context = context;
//		this.stacks = getItems(context.getItemVariant().getNbt());
//	}
//
//	public boolean valid() {
//		return context.getItemVariant().isOf(item);
//	}
//
//	@Override
//	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
//		if (!resource.isBlank() && resource.getItem().canFitInsideContainerItems()) {
//			int currentWeight = getContentWeight();
//			int insertedWeight = getWeight(resource, maxAmount);
//			long toInsert = Math.min(maxAmount, (64 - currentWeight) / insertedWeight);
//			if (toInsert == 0) {
//				return 0;
//			} else {
//				Optional<ItemStack> existing = getMatchingItem(resource);
//				if (existing.isPresent()) {
//					ItemStack existingStack = existing.get();
//					existingStack.grow((int) toInsert);
//					existingStack.save(compoundTag2);
//					listTag.remove(compoundTag2);
//					listTag.add(0, compoundTag2);
//				} else {
//					ItemStack itemStack2 = insertedStack.copy();
//					itemStack2.setCount(toInsert);
//					CompoundTag compoundTag3 = new CompoundTag();
//					itemStack2.save(compoundTag3);
//					listTag.add(0, compoundTag3);
//				}
//
//				return toInsert;
//			}
//		} else {
//			return 0;
//		}
//	}
//
//	@Override
//	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
//		return super.extract(resource, maxAmount, transaction);
//	}
//
//	@Override
//	protected void onFinalCommit() {
//		ItemVariant current = context.getItemVariant();
//		if (valid()) {
//			CompoundTag oldTag = current.copyNbt();
//			if (oldTag == null) oldTag = new CompoundTag();
//			oldTag.put("Items", getTag(stacks));
//		}
//	}
//
//	public int getContentWeight() {
//		int total = 0;
//		for (ItemStack stack : stacks) {
//			total += BundleItemAccessor.port_lib$getWeight(stack) * stack.getCount();
//		}
//		return total;
//	}
//
//	private static int getWeight(ItemVariant variant, long amount) {
//		if (variant.isOf(Items.BUNDLE)) {
//			return 4 + BundleItemAccessor.port_lib$getWeight(variant.toStack((int) amount));
//		} else {
//			if ((variant.isOf(Items.BEEHIVE) || variant.isOf(Items.BEE_NEST)) && variant.hasNbt()) {
//				CompoundTag blockEntityTag = variant.getNbt().getCompound("BlockEntityTag");
//				if (blockEntityTag != null && !blockEntityTag.getList("Bees", 10).isEmpty()) {
//					return 64;
//				}
//			}
//
//			return 64 / variant.getItem().getMaxStackSize();
//		}
//	}
//
//	private Optional<ItemStack> getMatchingItem(ItemVariant variant) {
//		ItemStack varStack = variant.toStack();
//		for (ItemStack stack : stacks) {
//			if (ItemStack.isSameItemSameTags(stack, varStack))
//				return Optional.of(stack);
//		}
//		return Optional.empty();
//	}
//
//	private static Optional<ItemStack> removeOne(ItemStack stack) {
//		CompoundTag compoundTag = stack.getOrCreateTag();
//		if (!compoundTag.contains("Items")) {
//			return Optional.empty();
//		} else {
//			ListTag listTag = compoundTag.getList("Items", 10);
//			if (listTag.isEmpty()) {
//				return Optional.empty();
//			} else {
//				int i = 0;
//				CompoundTag compoundTag2 = listTag.getCompound(0);
//				ItemStack itemStack = ItemStack.of(compoundTag2);
//				listTag.remove(0);
//				if (listTag.isEmpty()) {
//					stack.removeTagKey("Items");
//				}
//
//				return Optional.of(itemStack);
//			}
//		}
//	}
//
//	public static ItemStack[] getItems(@Nullable CompoundTag tag) {
//		if (tag == null) return new ItemStack[] {};
//		ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
//		return list.stream().map(t -> ItemStack.of((CompoundTag) t)).toArray(ItemStack[]::new);
//	}
//
//	public static CompoundTag getTag(ItemStack[] stacks) {
//		ListTag list = new ListTag();
//		for (ItemStack stack : stacks) {
//			list.add(stack.save(new CompoundTag()));
//		}
//		CompoundTag tag = new CompoundTag();
//		tag.put("Items", list);
//		return tag;
//	}
//}

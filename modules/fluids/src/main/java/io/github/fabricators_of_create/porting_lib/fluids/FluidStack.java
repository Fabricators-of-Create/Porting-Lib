package io.github.fabricators_of_create.porting_lib.fluids;

import java.util.Objects;
import java.util.Optional;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidStack {
	public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BuiltInRegistries.FLUID.byNameCodec().fieldOf("FluidName").forGetter(FluidStack::getFluid),
					Codec.LONG.fieldOf("Amount").forGetter(FluidStack::getAmount),
					CompoundTag.CODEC.optionalFieldOf("VariantTag", null).forGetter(fluidStack -> fluidStack.getType().copyNbt()),
					CompoundTag.CODEC.optionalFieldOf("Tag").forGetter(stack -> Optional.ofNullable(stack.getTag()))
			).apply(instance, (fluid, amount, variantTag, tag) -> {
				FluidStack stack = new FluidStack(fluid, amount, variantTag);
				tag.ifPresent(stack::setTag);
				return stack;
			})
	);

	public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0) {
		@Override
		public FluidStack setAmount(long amount) {
			return this;
		}

		@Override
		public void shrink(int amount) {
		}

		@Override
		public void shrink(long amount) {
		}

		@Override
		public void grow(long amount) {
		}

		@Override
		public void setTag(CompoundTag tag) {
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public FluidStack copy() {
			return this;
		}
	};

	private final FluidVariant type;
	@Nullable
	private CompoundTag tag;
	private long amount;

	public FluidStack(FluidVariant type, long amount) {
		this.type = type;
		this.amount = amount;
		this.tag = type.copyNbt();
	}

	public FluidStack(FluidVariant type, long amount, @Nullable CompoundTag tag) {
		this(type, amount);
		this.tag = tag;
	}

	public FluidStack(StorageView<FluidVariant> view) {
		this(view.getResource(), view.getAmount());
	}

	public FluidStack(ResourceAmount<FluidVariant> resource) {
		this(resource.resource(), resource.amount());
	}

	/**
	 * Avoid this constructor when possible, may result in NBT loss
	 */
	public FluidStack(Fluid type, long amount) {
		this(FluidVariant.of(type instanceof FlowingFluid flowing ? flowing.getSource() : type), amount);
	}

	public FluidStack(Fluid type, long amount, @Nullable CompoundTag nbt) {
		this(FluidVariant.of(type instanceof FlowingFluid flowing ? flowing.getSource() : type, nbt), amount);
		this.tag = nbt;
	}

	public FluidStack(FluidStack copy, long amount) {
		this(copy.getType(), amount);
		if (copy.hasTag()) tag = copy.getTag().copy();
	}

	public FluidStack setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public void grow(long amount) {
		setAmount(getAmount() + amount);
	}

	public FluidVariant getType() {
		return type;
	}

	public Fluid getFluid() {
		return getType().getFluid();
	}

	public long getAmount() {
		return amount;
	}

	public boolean isEmpty() {
		return amount <= 0 || getType().isBlank();
	}

	public void shrink(int amount) {
		setAmount(getAmount() - amount);
	}

	public void shrink(long amount) {
		setAmount(getAmount() - amount);
	}

	/**
	 * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
	 *
	 * @param other
	 *            The FluidStack for comparison
	 * @return true if the Fluids (IDs and NBT Tags) are the same
	 */
	public boolean isFluidEqual(FluidStack other) {
		if (this == other) return true;
		return isFluidEqual(other.getType());
	}

	public boolean isFluidEqual(FluidVariant other) {
		return isFluidEqual(getType(), other);
	}

	public static boolean isFluidEqual(FluidVariant mine, FluidVariant other) {
		if (mine == other) return true;
		if (other == null) return false;

		boolean fluidsEqual = mine.isOf(other.getFluid());

		CompoundTag myTag = mine.getNbt();
		CompoundTag theirTag = other.getNbt();
		boolean tagsEqual = Objects.equals(myTag, theirTag);

		return fluidsEqual && tagsEqual;
	}

	public boolean canFill(FluidVariant var) {
		return isEmpty() || var.isOf(getFluid()) && Objects.equals(var.getNbt(), getType().getNbt());
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt.put("Variant", getType().toNbt());
		nbt.putLong("Amount", getAmount());
		if (tag != null)
			nbt.put("Tag", tag);
		return nbt;
	}

	public static FluidStack loadFluidStackFromNBT(CompoundTag tag) {
		FluidStack stack;
		if (tag.contains("FluidName")) { // legacy forge loading
			Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(tag.getString("FluidName")));
			int amount = tag.getInt("Amount");
			if (tag.contains("Tag")) {
				stack = new FluidStack(fluid, amount, tag.getCompound("Tag"));
			} else {
				stack = new FluidStack(fluid, amount);
			}
		} else {
			CompoundTag fluidTag = tag.getCompound("Variant");
			FluidVariant fluid = FluidVariant.fromNbt(fluidTag);
			stack = new FluidStack(fluid, tag.getLong("Amount"));
			if(tag.contains("Tag", Tag.TAG_COMPOUND))
				stack.tag = tag.getCompound("Tag");
		}

		return stack;
	}

	public void setTag(CompoundTag tag) {
		this.tag = tag;
	}

	@Nullable
	public CompoundTag getTag() {
		return tag;
	}

	public CompoundTag getOrCreateTag() {
		if (tag == null) tag = new CompoundTag();
		return tag;
	}

	public void removeChildTag(String key) {
		if (getTag() == null) return;
		getTag().remove(key);
	}

	public Component getDisplayName() {
		return FluidVariantAttributes.getName(this.type);
	}

	public boolean hasTag() {
		return tag != null;
	}

	public static FluidStack readFromPacket(FriendlyByteBuf buffer) {
		FluidVariant fluid = FluidVariant.fromPacket(buffer);
		long amount = buffer.readVarLong();
		CompoundTag tag = buffer.readNbt();
		if (fluid.isBlank()) return EMPTY;
		return new FluidStack(fluid, amount, tag);
	}

	public FriendlyByteBuf writeToPacket(FriendlyByteBuf buffer) {
		getType().toPacket(buffer);
		buffer.writeVarLong(getAmount());
		buffer.writeNbt(getTag());
		return buffer;
	}

	public FluidStack copy() {
		CompoundTag tag = getTag();
		if (tag != null) tag = tag.copy();
		return new FluidStack(getType(), getAmount(), tag);
	}

	private boolean isFluidStackTagEqual(FluidStack other) {
		return tag == null ? other.tag == null : other.tag != null && tag.equals(other.tag);
	}

	/**
	 * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
	 */
	public static boolean areFluidStackTagsEqual(@NotNull FluidStack stack1, @NotNull FluidStack stack2) {
		return stack1.isFluidStackTagEqual(stack2);
	}

	/**
	 * Determines if the Fluids are equal and this stack is larger.
	 *
	 * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
	 */
	public boolean containsFluid(@NotNull FluidStack other) {
		return isFluidEqual(other) && amount >= other.amount;
	}

	/**
	 * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
	 *
	 * @param other
	 *            - the FluidStack for comparison
	 * @return true if the two FluidStacks are exactly the same
	 */
	public boolean isFluidStackIdentical(FluidStack other) {
		return isFluidEqual(other) && amount == other.amount;
	}

	/**
	 * Determines if the FluidIDs and NBT Tags are equal compared to a registered container
	 * ItemStack. This does not check amounts.
	 *
	 * @param other
	 *            The ItemStack for comparison
	 * @return true if the Fluids (IDs and NBT Tags) are the same
	 */
	public boolean isFluidEqual(@NotNull ItemStack other) {
		Storage<FluidVariant> storage = FluidStorage.ITEM.find(other, ContainerItemContext.withInitial(other));;
		if (storage == null)
			return false;
		return new FluidStack(StorageUtil.findExtractableContent(storage, null)).isFluidEqual(this);
	}

	@Override
	public final int hashCode() {
		long code = 1;
		code = 31 * code + getFluid().hashCode();
		code = 31 * code + amount;
		if (tag != null)
			code = 31 * code + tag.hashCode();
		return (int) code;
	}

	/**
	 * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
	 *
	 * This is included for use in data structures.
	 */
	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof FluidStack)) {
			return false;
		}
		return isFluidEqual((FluidStack) o);
	}
}

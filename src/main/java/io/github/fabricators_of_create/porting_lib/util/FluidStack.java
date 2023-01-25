package io.github.fabricators_of_create.porting_lib.util;

import java.util.Objects;
import java.util.Optional;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

@SuppressWarnings({"UnstableApiUsage"})
public class FluidStack {
	public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Registry.FLUID.byNameCodec().fieldOf("FluidName").forGetter(FluidStack::getFluid),
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

	public boolean isFluidEqual(FluidStack other) {
		if (this == other) return true;
		return isFluidEqual(other.getType());
	}

	public boolean isFluidEqual(FluidVariant other) {
		if (this.getType() == other) return true;
		if (other == null) return false;

		FluidVariant mine = getType();
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
			Fluid fluid = Registry.FLUID.get(new ResourceLocation(tag.getString("FluidName")));
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

	public static FluidStack fromBuffer(FriendlyByteBuf buffer) {
		FluidVariant fluid = FluidVariant.fromPacket(buffer);
		long amount = buffer.readVarLong();
		CompoundTag tag = buffer.readNbt();
		if (fluid.isBlank()) return EMPTY;
		return new FluidStack(fluid, amount, tag);
	}

	public FriendlyByteBuf toBuffer(FriendlyByteBuf buffer) {
		return toBuffer(this, buffer);
	}

	public static FriendlyByteBuf toBuffer(FluidStack stack, FriendlyByteBuf buffer) {
		stack.getType().toPacket(buffer);
		buffer.writeVarLong(stack.getAmount());
		buffer.writeNbt(stack.tag);
		return buffer;
	}

	public FluidStack copy() {
		CompoundTag tag = getTag();
		if (tag != null) tag = tag.copy();
		return new FluidStack(getType(), getAmount(), tag);
	}
}

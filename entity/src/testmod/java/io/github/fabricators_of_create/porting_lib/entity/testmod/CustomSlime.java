package io.github.fabricators_of_create.porting_lib.entity.testmod;

import org.jetbrains.annotations.NotNull;

import io.github.fabricators_of_create.porting_lib.entity.CustomLandingEffectsSlime;
import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CustomSlime extends Slime implements CustomLandingEffectsSlime, ExtraSpawnDataEntity, MultiPartEntity {

	public final OrbitingItem item;
	public final PartEntity<?>[] parts;

	public CustomSlime(EntityType<? extends Slime> entityType, Level level) {
		super(entityType, level);
		this.item = new OrbitingItem(this, rand() ? Items.SLIME_BLOCK : Items.SLIME_BALL);
		this.parts = new OrbitingItem[] { item };
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buf) {
		buf.writeItem(item.stack);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buf) {
		item.stack = buf.readItem();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.put("Item", item.stack.save(new CompoundTag()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("Item", Tag.TAG_COMPOUND))
			item.stack = ItemStack.of(nbt.getCompound("Item"));
	}

	@Override
	public void tick() {
		super.tick();
		item.setPos(position().add(item.getOffset(0)));
		item.setOldPosAndRot();
	}

	@Override
	public boolean playLandingSound(SoundEvent squishSound, float volume, float pitch) {
		playSound(SoundEvents.GENERIC_EXPLODE, volume, pitch);
		return true;
	}

	@Override
	public PartEntity<?>[] getParts() {
		return parts;
	}

	private static boolean rand() {
		return Math.random() > 0.5;
	}

	public static class OrbitingItem extends PartEntity<CustomSlime> {
		public ItemStack stack;

		public OrbitingItem(CustomSlime parent, Item item) {
			super(parent);
			this.stack = new ItemStack(item);
		}

		@Override
		protected void defineSynchedData() {
		}

		@Override
		protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {
		}

		@Override
		protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {
		}

		public Vec3 getOffset(float tickDelta) {
			CustomSlime parent = getParent();
			float progress = parent.tickCount + tickDelta;
			return new Vec3(
					Math.cos(progress / 5) * 2,
					1,
					Math.sin(progress / 5) * 2
			);
		}
	}
}

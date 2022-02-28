package io.github.fabricators_of_create.porting_lib.util;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

public class LazySpawnEggItem extends SpawnEggItem {

	public static final List<LazySpawnEggItem> INSTANCES = new ArrayList<>();
	private static final Map<EntityType<? extends Mob>, LazySpawnEggItem> TYPE_MAP = new IdentityHashMap<>();
	private static final DispenseItemBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) -> {
		Direction face = source.getBlockState().getValue(DispenserBlock.FACING);
		EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack.getTag());

		try {
			type.spawn(source.getLevel(), stack, null, source.getPos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
		} catch (Exception exception) {
			DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
			return ItemStack.EMPTY;
		}

		stack.shrink(1);
		source.getLevel().gameEvent(GameEvent.ENTITY_PLACE, source.getPos());
		return stack;
	};
	private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

	public LazySpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
		super(null, backgroundColor, highlightColor, props);
		this.typeSupplier = type;

		INSTANCES.add(this);
		DispenseItemBehavior dispenseBehavior = this.createDispenseBehavior();
		if (dispenseBehavior != null) {
			DispenserBlock.registerBehavior(this, dispenseBehavior);
		}

		TYPE_MAP.put(this.typeSupplier.get(), this);
	}

	@Nullable
	public static SpawnEggItem fromEntityType(@Nullable EntityType<?> type) {
		SpawnEggItem ret = TYPE_MAP.get(type);
		return ret != null ? ret : SpawnEggItem.byId(type);
	}

	@Override
	public EntityType<?> getType(@Nullable CompoundTag tag) {
		EntityType<?> type = super.getType(tag);
		return type != null ? type : typeSupplier.get();
	}

	@Nullable
	protected DispenseItemBehavior createDispenseBehavior() {
		return DEFAULT_DISPENSE_BEHAVIOR;
	}
}

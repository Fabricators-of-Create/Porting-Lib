package io.github.fabricators_of_create.porting_lib.util;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class DeferredSpawnEggItem extends SpawnEggItem {
	public static final List<DeferredSpawnEggItem> MOD_EGGS = new ArrayList<>();
	private static final Map<EntityType<? extends Mob>, DeferredSpawnEggItem> TYPE_MAP = new IdentityHashMap<>();
	private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;

	public DeferredSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
		super(null, backgroundColor, highlightColor, props);
		this.typeSupplier = type;

		MOD_EGGS.add(this);
	}

	@Nullable
	protected DispenseItemBehavior createDispenseBehavior() {
		return DEFAULT_DISPENSE_BEHAVIOR;
	}

	@ApiStatus.Internal
	@Nullable
	public static SpawnEggItem deferredOnlyById(@Nullable EntityType<?> type) {
		return TYPE_MAP.get(type);
	}
	
	protected EntityType<?> getDefaultType() {
		return this.typeSupplier.get();
	}

	private static final DispenseItemBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) -> {
		Direction face = source.state().getValue(DispenserBlock.FACING);
		EntityType<?> type = ((SpawnEggItem) stack.getItem()).getType(stack);

		try {
			type.spawn(source.level(), stack, null, source.pos().relative(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
		} catch (Exception exception) {
			DispenseItemBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.pos(), exception);
			return ItemStack.EMPTY;
		}

		stack.shrink(1);
		source.level().gameEvent(GameEvent.ENTITY_PLACE, source.pos(), GameEvent.Context.of(source.state()));
		return stack;
	};

	@Override
	public EntityType<?> getType(ItemStack p_330335_) {
		CustomData customdata = p_330335_.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
		return !customdata.isEmpty() ? customdata.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(getDefaultType()) : getDefaultType();
	}

	@Override
	public FeatureFlagSet requiredFeatures() {
		return getDefaultType().requiredFeatures();
	}

	public static void init() {
		MOD_EGGS.forEach(egg -> {
			DispenseItemBehavior dispenseBehavior = egg.createDispenseBehavior();
			if (dispenseBehavior != null) {
				DispenserBlock.registerBehavior(egg, dispenseBehavior);
			}

			TYPE_MAP.put(egg.typeSupplier.get(), egg);
		});
	}
}

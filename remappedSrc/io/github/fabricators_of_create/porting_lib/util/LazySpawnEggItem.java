package io.github.fabricators_of_create.porting_lib.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class LazySpawnEggItem extends SpawnEggItem {

	private static final Map<EntityType<? extends MobEntity>, LazySpawnEggItem> TYPE_MAP = new IdentityHashMap<>();
	private static final DispenserBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) -> {
		Direction face = source.getBlockState().get(DispenserBlock.FACING);
		EntityType<?> type = ((SpawnEggItem) stack.getItem()).getEntityType(stack.getNbt());

		try {
			type.spawnFromItemStack(source.getWorld(), stack, null, source.getPos().offset(face), SpawnReason.DISPENSER, face != Direction.UP, false);
		} catch (Exception exception) {
			DispenserBehavior.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", source.getPos(), exception);
			return ItemStack.EMPTY;
		}

		stack.decrement(1);
		source.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, source.getPos());
		return stack;
	};
	private final Supplier<? extends EntityType<? extends MobEntity>> typeSupplier;

	public LazySpawnEggItem(Supplier<? extends EntityType<? extends MobEntity>> type, int backgroundColor, int highlightColor, Settings props) {
		super(null, backgroundColor, highlightColor, props);
		this.typeSupplier = type;

		DispenserBehavior dispenseBehavior = this.createDispenseBehavior();
		if (dispenseBehavior != null) {
			DispenserBlock.registerBehavior(this, dispenseBehavior);
		}

		TYPE_MAP.put(this.typeSupplier.get(), this);

		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> ColorProviderRegistry.ITEM.register((stack, layer) -> getColor(layer), this));
	}

	@Nullable
	public static SpawnEggItem fromEntityType(@Nullable EntityType<?> type) {
		SpawnEggItem ret = TYPE_MAP.get(type);
		return ret != null ? ret : SpawnEggItem.forEntity(type);
	}

	@Override
	public EntityType<?> getEntityType(@Nullable NbtCompound tag) {
		EntityType<?> type = super.getEntityType(tag);
		return type != null ? type : typeSupplier.get();
	}

	@Nullable
	protected DispenserBehavior createDispenseBehavior() {
		return DEFAULT_DISPENSE_BEHAVIOR;
	}
}

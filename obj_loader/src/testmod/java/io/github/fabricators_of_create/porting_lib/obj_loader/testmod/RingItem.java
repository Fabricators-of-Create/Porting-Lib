package io.github.fabricators_of_create.porting_lib.obj_loader.testmod;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;

public class RingItem extends Item implements Equipable {
	public RingItem(Properties properties) {
		super(properties);
	}

	@Override
	@NotNull
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}
}

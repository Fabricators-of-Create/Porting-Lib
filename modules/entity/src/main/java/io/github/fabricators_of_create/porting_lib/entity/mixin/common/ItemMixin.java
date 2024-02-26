package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.extensions.ItemExtensions;
import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ItemExtensions {
}

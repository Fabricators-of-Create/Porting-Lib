package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.ItemExtensions;
import net.minecraft.world.item.Item;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemExtensions {
}

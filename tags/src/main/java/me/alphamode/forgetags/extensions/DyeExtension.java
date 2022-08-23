package me.alphamode.forgetags.extensions;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface DyeExtension {
    default TagKey<Item> getTag() {
        return null;
    }
}

package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.MobEffectExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.MobEffectInstanceExtensions;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceExtensions {
	@Shadow
	public abstract MobEffect getEffect();

	private java.util.List<net.minecraft.world.item.ItemStack> curativeItems;

	@Override
	public java.util.List<net.minecraft.world.item.ItemStack> getCurativeItems() {
		if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
			this.curativeItems = ((MobEffectExtensions)getEffect()).getCurativeItems();
		return this.curativeItems;
	}
	@Override
	public void setCurativeItems(java.util.List<net.minecraft.world.item.ItemStack> curativeItems) {
		this.curativeItems = curativeItems;
	}
	private static MobEffectInstance readCurativeItems(MobEffectInstance effect, CompoundTag nbt) {
		if (nbt.contains("CurativeItems", net.minecraft.nbt.Tag.TAG_LIST)) {
			java.util.List<net.minecraft.world.item.ItemStack> items = new java.util.ArrayList<net.minecraft.world.item.ItemStack>();
			net.minecraft.nbt.ListTag list = nbt.getList("CurativeItems", net.minecraft.nbt.Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				items.add(net.minecraft.world.item.ItemStack.of(list.getCompound(i)));
			}
			((MobEffectInstanceExtensions)effect).setCurativeItems(items);
		}

		return effect;
	}
}

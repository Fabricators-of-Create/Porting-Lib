package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.MobEffectInstanceExtensions;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StatusEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceExtensions {
	@Shadow
	public abstract StatusEffect getEffect();

	private java.util.List<net.minecraft.item.ItemStack> curativeItems;

	@Override
	public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
		if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
			this.curativeItems = getEffect().getCurativeItems();
		return this.curativeItems;
	}
	@Override
	public void setCurativeItems(java.util.List<net.minecraft.item.ItemStack> curativeItems) {
		this.curativeItems = curativeItems;
	}
	private static StatusEffectInstance readCurativeItems(StatusEffectInstance effect, NbtCompound nbt) {
		if (nbt.contains("CurativeItems", net.minecraft.nbt.NbtElement.LIST_TYPE)) {
			java.util.List<net.minecraft.item.ItemStack> items = new java.util.ArrayList<net.minecraft.item.ItemStack>();
			net.minecraft.nbt.NbtList list = nbt.getList("CurativeItems", net.minecraft.nbt.NbtElement.COMPOUND_TYPE);
			for (int i = 0; i < list.size(); i++) {
				items.add(net.minecraft.item.ItemStack.fromNbt(list.getCompound(i)));
			}
			effect.setCurativeItems(items);
		}

		return effect;
	}
}

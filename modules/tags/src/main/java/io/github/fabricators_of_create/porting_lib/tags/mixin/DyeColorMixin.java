package io.github.fabricators_of_create.porting_lib.tags.mixin;

import net.minecraft.world.level.material.MapColor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.tags.extensions.DyeExtension;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

@Mixin(DyeColor.class)
public class DyeColorMixin implements DyeExtension {
	@Shadow
	@Final
	private String name;
	@Unique
	private TagKey<Item> dyesTag;
	@Unique
	private TagKey<Item> dyedTag;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void addTag(String enumName, int ordinal, int id, String name, int diffuseColor, MapColor mapColor, int fireworkColor, int textColor, CallbackInfo ci) {
		this.dyesTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyes/" + name));
		this.dyedTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dyed/" + name));
	}

	@Override
	public TagKey<Item> getTag() {
		return dyesTag;
	}

	@Override
	public TagKey<Item> getDyedTag() {
		return dyedTag;
	}
}

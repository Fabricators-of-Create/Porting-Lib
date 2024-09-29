package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.item.CustomMapItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin<T extends ItemFrame> extends EntityRenderer<T> {
	protected ItemFrameRendererMixin(Context context) {
		super(context);
	}

	@WrapOperation(
			method = "getFrameModelResourceLoc",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
	)
	private boolean port_lib$customMapsAreMaps(ItemStack instance, Item item, Operation<Boolean> original) {
		if (item instanceof CustomMapItem)
			return true;
		return original.call(instance, item);
	}
}

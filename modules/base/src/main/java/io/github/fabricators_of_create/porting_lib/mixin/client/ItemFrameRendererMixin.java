package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.item.CustomMapItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin<T extends ItemFrame> extends EntityRenderer<T> {
	protected ItemFrameRendererMixin(Context context) {
		super(context);
	}

//	@ModifyArgs( TODO: PORT
//			method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
//	)
//	private void port_lib$customMapsAreMaps(Args args, T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
//		ItemStack stack = entity.getItem();
//		Item item = stack.getItem();
//		if (item instanceof CustomMapItem) {
//			args.set(0, item);
//		}
//	}

	@ModifyArgs(
			method = "getFrameModelResourceLoc",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
	)
	private void port_lib$customMapsAreMaps2(Args args, T entity, ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof CustomMapItem) {
			args.set(0, item);
		}
	}

	@ModifyVariable(
			method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/MapItem;getSavedData(Lnet/minecraft/world/level/saveddata/maps/MapId;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;")
	)
	private MapItemSavedData port_lib$getCorrectMapData(MapItemSavedData original, T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
		ItemStack stack = entity.getItem();
		if (stack.getItem() instanceof CustomMapItem custom) {
			return custom.getCustomMapData(stack, entity.level());
		}
		return original;
	}
}

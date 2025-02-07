package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.util.CustomMapItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameRendererMixin<T extends ItemFrameEntity> extends EntityRenderer<T> {
	protected ItemFrameRendererMixin(Context context) {
		super(context);
	}

	@ModifyArgs(
			method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
	)
	private void port_lib$customMapsAreMaps(Args args, T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
		ItemStack stack = entity.getHeldItemStack();
		Item item = stack.getItem();
		if (item instanceof CustomMapItem) {
			args.set(0, item);
		}
	}

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
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/MapItem;getSavedData(Ljava/lang/Integer;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData;")
	)
	private MapState port_lib$getCorrectMapData(MapState original, T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
		ItemStack stack = entity.getHeldItemStack();
		if (stack.getItem() instanceof CustomMapItem custom) {
			return custom.getCustomMapData(stack, entity.world);
		}
		return original;
	}
}

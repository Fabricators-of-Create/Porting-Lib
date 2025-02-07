package io.github.fabricators_of_create.porting_lib.mixin.common.hack;

import me.alphamode.forgetags.Tags;

import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ForgeTags is not being updated as it has been merged into newer Porting Lib versions.
 * Gross fix for #70
 */
@Mixin(value = Tags.Blocks.class, remap = false)
public class Tags$BlocksMixin {
	@Inject(method = "tag", at = @At("HEAD"), cancellable = true)
	private static void useFapiTags(String name, CallbackInfoReturnable<TagKey<Block>> cir) {
		if ("needs_netherite_tool".equals(name)) {
			TagKey<Block> fapiTag = MiningLevelManager.getBlockTag(ToolMaterials.NETHERITE.getMiningLevel());
			cir.setReturnValue(fapiTag);
		}
	}
}

package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.util.datafix.DataFixers;

import net.minecraft.util.datafix.fixes.AttributesRename;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

@Mixin(DataFixers.class)
public abstract class DataFixersMixin {
	@Shadow
	@Final
	private static BiFunction<Integer, Schema, Schema> SAME_NAMESPACED;

	@Shadow
	protected static UnaryOperator<String> createRenamer(Map<String, String> map) {
		return null;
	}

	// Honestly we don't really need this, but it's nice for an end user.
	@Inject(method = "addFixers", at = @At("TAIL"))
	private static void addReachFixer(DataFixerBuilder dataFixerBuilder, CallbackInfo ci) {
		// Neo: rename neo attributes to new MC attributes
		// Happens in 24w03a
		Schema neoSchema3804 = dataFixerBuilder.addSchema(3804, SAME_NAMESPACED);
		dataFixerBuilder.addFixer(new AttributesRename(
				neoSchema3804,
				"(Neo) Rename reach attributes to vanilla",
				createRenamer(ImmutableMap.of(
						"neoforge:entity_reach", "minecraft:player.entity_interaction_range",
						"neoforge:block_reach", "minecraft:player.block_interaction_range"
				))
		));
	}
}

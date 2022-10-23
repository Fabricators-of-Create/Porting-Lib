package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import net.minecraft.client.renderer.Sheets;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.WoodType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sheets.class)
public interface SheetsAccessor {
	@Invoker("createSignMaterial")
	static Material port_lib$createSignMaterial(WoodType woodType) {
		throw new RuntimeException("mixin failed!");
	}
}

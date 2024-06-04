package io.github.fabricators_of_create.porting_lib.gui.utils;

import net.minecraft.client.gui.components.Button;

public class ModdedButton extends Button {
	protected ModdedButton(Builder builder) {
		super(builder.x, builder.y, builder.width, builder.height, builder.message, builder.onPress, builder.createNarration);
		setTooltip(builder.tooltip); // Forge: Make use of the Builder tooltip
	}
}

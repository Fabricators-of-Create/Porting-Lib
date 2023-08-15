package io.github.fabricators_of_create.porting_lib.attributes.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AttributeInstance.class)
public interface AttributeInstanceAccessor {
	@Invoker
	void callRemoveModifier(AttributeModifier modifier);
}

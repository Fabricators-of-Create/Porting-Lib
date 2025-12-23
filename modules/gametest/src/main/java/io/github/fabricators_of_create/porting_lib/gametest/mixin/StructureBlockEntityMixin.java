package io.github.fabricators_of_create.porting_lib.gametest.mixin;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.gametest.extensions.StructureBlockEntityExtensions;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

@Mixin(StructureBlockEntity.class)
public class StructureBlockEntityMixin implements StructureBlockEntityExtensions {
	@Unique
	private String qualifiedTestName;

	@Override
	public void setQualifiedTestName(String qualifiedTestName) {
		this.qualifiedTestName = qualifiedTestName;
	}

	@Override
	public String getQualifiedTestName() {
		return qualifiedTestName;
	}

	@Inject(method = "saveAdditional", at = @At("RETURN"))
	private void saveQualifiedTestName(ValueOutput output, CallbackInfo ci) {
		if (qualifiedTestName != null)
			output.putString("PortingLib$ExtendedGameTestFunction", qualifiedTestName);
	}

	@Inject(method = "loadAdditional", at = @At("RETURN"))
	private void loadQualifiedTestName(ValueInput input, CallbackInfo ci) {
		this.qualifiedTestName = input.getString("PortingLib$ExtendedGameTestFunction").orElse(null);
	}
}

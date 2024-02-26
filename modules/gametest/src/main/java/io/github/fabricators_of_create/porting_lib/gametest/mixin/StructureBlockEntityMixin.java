package io.github.fabricators_of_create.porting_lib.gametest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.gametest.extensions.StructureBlockEntityExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
	private void saveQualifiedTestName(CompoundTag nbt, CallbackInfo ci) {
		if (qualifiedTestName != null)
			nbt.putString("PortingLib$ExtendedGameTestFunction", qualifiedTestName);
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void loadQualifiedTestName(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains("PortingLib$ExtendedGameTestFunction", Tag.TAG_STRING))
			this.qualifiedTestName = nbt.getString("PortingLib$ExtendedGameTestFunction");
	}
}

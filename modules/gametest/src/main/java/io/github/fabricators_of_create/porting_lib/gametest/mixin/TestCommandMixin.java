package io.github.fabricators_of_create.porting_lib.gametest.mixin;

import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.ExtendedTestFunction;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.gametest.extensions.StructureBlockEntityExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

import java.util.Optional;

@Mixin(TestCommand.class)
public class TestCommandMixin {
	@WrapOperation(
			method = "runTest(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/gametest/framework/MultipleTestTracker;Z)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/gametest/framework/GameTestRegistry;findTestFunction(Ljava/lang/String;)Ljava/util/Optional;"
			),
			require = 0 // non-critical
	)
	private static Optional<TestFunction> getCorrectTestFunction(String testName, Operation<Optional<TestFunction>> original,
																 ServerLevel level, BlockPos pos, @Nullable MultipleTestTracker tracker,
																 @Local StructureBlockEntity structureBlock) {
		String qualifiedTestName = ((StructureBlockEntityExtensions) structureBlock).getQualifiedTestName();
		if (qualifiedTestName == null)
			return original.call(testName);
		ExtendedTestFunction function = ExtendedTestFunction.NAMES_TO_FUNCTIONS.get(qualifiedTestName);
		if (function == null)
			throw new IllegalStateException("Could not find test function " + qualifiedTestName);
		return Optional.of(function);
	}
}

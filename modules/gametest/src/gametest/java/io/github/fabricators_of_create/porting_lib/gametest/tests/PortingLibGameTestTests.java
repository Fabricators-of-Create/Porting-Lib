package io.github.fabricators_of_create.porting_lib.gametest.tests;

import java.lang.reflect.Method;

import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.PortingLibGameTestHelper;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

public class PortingLibGameTestTests implements CustomTestMethodInvoker {
	@Override
	public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
		method.invoke(this, new PortingLibGameTestHelper(context.testInfo)); // I don't know if we can just make our own helper like this
	}

	@GameTest(structure = "porting_lib_gametest:test_testing/test")
	public static void test(PortingLibGameTestHelper helper) {
		BlockPos grass = new BlockPos(0, 1, 0);
		helper.assertBlockPresent(Blocks.GRASS_BLOCK, grass);
		BlockPos flower = grass.above();
		helper.setBlock(flower, Blocks.POPPY);
		helper.succeedWhen(() -> {
			helper.assertSecondsPassed(2);
			helper.assertBlockPresent(Blocks.POPPY, flower);
		});
	}
}

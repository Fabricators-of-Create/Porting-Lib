package io.github.fabricators_of_create.porting_lib.gametest.tests;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.CustomGameTestHelper;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.GameTestGroup;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.PortingLibGameTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;

public class PortingLibGameTestTests {
	@GameTestGenerator
	public static Collection<TestFunction> generateTests() {
		return PortingLibGameTest.getTestsFrom(Tests.class);
	}

	@CustomGameTestHelper(PortingLibGameTestHelper.class)
	@GameTestGroup(namespace = "porting_lib_gametest", path = "test_testing")
	public static class Tests {
		@GameTest(template = "test")
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
}

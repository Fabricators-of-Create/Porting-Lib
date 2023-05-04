package io.github.fabricators_of_create.porting_lib.gametest.tests;

import java.util.Collection;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.CustomGameTestHelper;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.PortingLibGameTestHelper;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.GameTestGroup;
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
			helper.assertBlockPresent(Blocks.DIRT, new BlockPos(2, 1, 2));
			helper.succeed();
		}
	}
}

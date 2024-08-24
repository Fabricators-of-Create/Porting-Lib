package io.github.fabricators_of_create.porting_lib.gametest.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.extensions.StructureBlockEntityExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;

/**
 * An extension to game tests implementing functionality for {@link CustomGameTestHelper} and {@link GameTestGroup}.
 * To use, create a {@link GameTestGenerator} that provides tests using {@link PortingLibGameTest#getTestsFrom(Class[])}.
 */
public class ExtendedTestFunction {
	@Internal
	public static final Map<String, ExtendedTestFunction> NAMES_TO_FUNCTIONS = new HashMap<>();

	public final String fullName;
	public final String simpleName;
	public final TestFunction testFunction;

	protected ExtendedTestFunction(String fullName, String simpleName, String pBatchName,
								 String pStructureName, Rotation pRotation, int pMaxTicks, long pSetupTicks,
								 boolean pRequired, int pMaxAttempts, int pRequiredSuccesses, Consumer<GameTestHelper> pFunction) {
		testFunction = new TestFunction(pBatchName, simpleName, pStructureName, pRotation, pMaxTicks, pSetupTicks, pRequired, false, pMaxAttempts, pRequiredSuccesses, true, pFunction);
		this.fullName = fullName;
		this.simpleName = simpleName;
		NAMES_TO_FUNCTIONS.put(fullName, this);
	}

	@Nullable
	public static TestFunction of(Method method) {
		GameTest gt = method.getAnnotation(GameTest.class);
		if (gt == null) // skip non-test methods
			return null;
		Class<?> owner = method.getDeclaringClass();
		String qualifiedName = owner.getSimpleName() + "." + method.getName();
		CustomGameTestHelper customHelper = getCustomHelper(method, owner);
		validateTestMethod(method, gt, owner, customHelper, qualifiedName);

		String structure = getStructure(gt, owner);
		Rotation rotation = StructureUtils.getRotationForRotationSteps(gt.rotationSteps());

		String fullName = owner.getName() + '.' + method.getName();
		String simpleName = owner.getSimpleName() + '.' + method.getName();
		return new ExtendedTestFunction(
				// use structure for test name since that's what MC fills structure blocks with for some reason
				fullName, simpleName, gt.batch(), structure, rotation, gt.timeoutTicks(), gt.setupTicks(),
				gt.required(), gt.attempts(), gt.requiredSuccesses(), run(fullName, asConsumer(method))
		).testFunction;
	}

	private static void validateTestMethod(Method method, GameTest gt, Class<?> owner, CustomGameTestHelper customHelper, String qualifiedName) {
		if (gt.template().isEmpty())
			throw new IllegalArgumentException(qualifiedName + " must provide a template structure");

		int modifiers = method.getModifiers();
		if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers))
			throw new IllegalArgumentException(qualifiedName + " must be public and static");

		if (!Modifier.isPublic(owner.getModifiers()))
			throw new IllegalStateException(owner.getName() + " must be public");

		if (method.getReturnType() != void.class)
			throw new IllegalArgumentException(qualifiedName + " must return void");

		Class<? extends GameTestHelper> helperClass = customHelper != null ? customHelper.value() : GameTestHelper.class;
		if (method.getParameterCount() != 1 || method.getParameterTypes()[0] != helperClass)
			throw new IllegalArgumentException(qualifiedName + " must take 1 parameter of type " + helperClass.getSimpleName());
	}

	private static String getStructure(GameTest gt, Class<?> owner) {
		GameTestGroup group = owner.getAnnotation(GameTestGroup.class);
		String structure = gt.template();
		if (group == null || structure.contains(":")) // override group if a full ID
			return structure;
		String groupDir = group.path();
		String path = groupDir.isEmpty() ? structure : groupDir + '/' + structure;
		// namespace:gametest/path/structure || namespace:gametest/structure
		return group.namespace() + ":gametest/" + path;
	}

	private static CustomGameTestHelper getCustomHelper(Method method, Class<?> owner) {
		CustomGameTestHelper methodCustom = method.getAnnotation(CustomGameTestHelper.class);
		return methodCustom != null ? methodCustom : owner.getAnnotation(CustomGameTestHelper.class);
	}

	private static Consumer<GameTestHelper> asConsumer(Method method) {
		return (helper) -> {
			try {
				method.invoke(null, helper);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		};
	}

	public static Consumer<GameTestHelper> run(String fullName, @NotNull Consumer<GameTestHelper> helper) {
		return consumer -> {
			helper.andThen(gameTestHelper -> {
				// give structure block test info
				StructureBlockEntityExtensions be = (StructureBlockEntityExtensions) gameTestHelper.getBlockEntity(BlockPos.ZERO);
				be.setQualifiedTestName(fullName);
			}).accept(consumer);
		};
	}
}

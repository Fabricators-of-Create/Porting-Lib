package io.github.fabricators_of_create.porting_lib.gametest.infrastructure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import io.github.fabricators_of_create.porting_lib.gametest.PortingLibGameTest;
import io.github.fabricators_of_create.porting_lib.gametest.extensions.StructureBlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.gametest.mixin.GameTestHelperAccessor;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;

/**
 * An extension to game tests implementing functionality for {@link CustomGameTestHelper} and {@link GameTestGroup}.
 * To use, create a {@link GameTestGenerator} that provides tests using {@link PortingLibGameTest#getTestsFrom(Class[])}.
 */
public class ExtendedTestFunction extends TestFunction {
	@Internal
	public static final Map<String, ExtendedTestFunction> NAMES_TO_FUNCTIONS = new HashMap<>();

	public final String fullyQualifiedName;
	private final UnaryOperator<GameTestHelper> helperProcessor;

	protected ExtendedTestFunction(String fullyQualifiedName, UnaryOperator<GameTestHelper> helperProcessor, String pBatchName,
								   String pTestName, String pStructureName, Rotation pRotation, int pMaxTicks, long pSetupTicks,
								   boolean pRequired, int pRequiredSuccesses, int pMaxAttempts, Consumer<GameTestHelper> pFunction) {
		super(pBatchName, pTestName, pStructureName, pRotation, pMaxTicks, pSetupTicks, pRequired, pRequiredSuccesses, pMaxAttempts, pFunction);
		this.fullyQualifiedName = fullyQualifiedName;
		NAMES_TO_FUNCTIONS.put(fullyQualifiedName, this);
		this.helperProcessor = helperProcessor;
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
		UnaryOperator<GameTestHelper> helperProcessor = getHelperProcessor(customHelper);

		String structure = getStructure(gt, owner);
		Rotation rotation = StructureUtils.getRotationForRotationSteps(gt.rotationSteps());

		String fullyQualifiedName = owner.getName() + "." + method.getName();
		return new ExtendedTestFunction(
				// use structure for test name since that's what MC fills structure blocks with for some reason
				fullyQualifiedName, helperProcessor, gt.batch(), structure, structure, rotation, gt.timeoutTicks(),
				gt.setupTicks(), gt.required(), gt.requiredSuccesses(), gt.attempts(), asConsumer(method)
		);
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

	private static UnaryOperator<GameTestHelper> getHelperProcessor(CustomGameTestHelper custom) {
		if (custom == null)
			return helper -> helper;
		Class<? extends GameTestHelper> klass = custom.value();
		try {
			Constructor<? extends GameTestHelper> constructor = klass.getConstructor(GameTestInfo.class);
			constructor.setAccessible(true);
			return helper -> {
				GameTestHelperAccessor access = (GameTestHelperAccessor) helper;
				GameTestInfo info = access.getTestInfo();
				try {
					GameTestHelper newHelper = constructor.newInstance(info);
					GameTestHelperAccessor newAccess = (GameTestHelperAccessor) newHelper;
					newAccess.setFinalCheckAdded(access.getFinalCheckAdded());
					return newHelper;
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
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

	@Override
	public void run(@NotNull GameTestHelper helper) {
		// give structure block test info for runthis
		StructureBlockEntityExtensions be = (StructureBlockEntityExtensions) helper.getBlockEntity(BlockPos.ZERO);
		be.setQualifiedTestName(fullyQualifiedName);
		super.run(helperProcessor.apply(helper));
	}
}

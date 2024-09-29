package io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableBiMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.struct.Target.Extension;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;

/**
 * based on ModifyExpressionValue
 * Wrap a variable, accessed either from a local, an array, or after a cast.
 * examples:
 * - i -> wrap(i)
 * - array[i] -> wrap(array[i])
 * - (float) i -> wrap((float) i)
 */
public class WrapVariableInjector extends Injector {
	public static final Type OBJECT_TYPE = InstructionAdapter.OBJECT_TYPE;
	private static final Map<Integer, Type> OPCODE_TYPES = ImmutableBiMap.<Integer, Type>builder()
			.put(Opcodes.ALOAD, OBJECT_TYPE)
			.put(Opcodes.DLOAD, Type.DOUBLE_TYPE)
			.put(Opcodes.FLOAD, Type.FLOAT_TYPE)
			.put(Opcodes.ILOAD, Type.INT_TYPE)
			.put(Opcodes.LLOAD, Type.LONG_TYPE)
			// arrays
			.put(Opcodes.AALOAD, OBJECT_TYPE)
			.put(Opcodes.BALOAD, Type.BOOLEAN_TYPE)
			.put(Opcodes.CALOAD, Type.CHAR_TYPE)
			.put(Opcodes.DALOAD, Type.DOUBLE_TYPE)
			.put(Opcodes.FALOAD, Type.FLOAT_TYPE)
			.put(Opcodes.IALOAD, Type.INT_TYPE)
			.put(Opcodes.LALOAD, Type.LONG_TYPE)
			.put(Opcodes.SALOAD, Type.SHORT_TYPE)
			// casts
			.put(Opcodes.CHECKCAST, OBJECT_TYPE)
			.put(Opcodes.I2L, Type.LONG_TYPE)
			.put(Opcodes.I2F, Type.FLOAT_TYPE)
			.put(Opcodes.I2D, Type.DOUBLE_TYPE)
			.put(Opcodes.L2I, Type.INT_TYPE)
			.put(Opcodes.L2F, Type.FLOAT_TYPE)
			.put(Opcodes.L2D, Type.DOUBLE_TYPE)
			.put(Opcodes.F2I, Type.INT_TYPE)
			.put(Opcodes.F2L, Type.FLOAT_TYPE)
			.put(Opcodes.F2D, Type.DOUBLE_TYPE)
			.put(Opcodes.D2I, Type.INT_TYPE)
			.put(Opcodes.D2L, Type.LONG_TYPE)
			.put(Opcodes.D2F, Type.FLOAT_TYPE)
			.put(Opcodes.I2B, Type.BOOLEAN_TYPE)
			.put(Opcodes.I2C, Type.CHAR_TYPE)
			.put(Opcodes.I2S, Type.SHORT_TYPE)
			.build();
	public static final Set<Integer> ALLOWED_OPCODES = OPCODE_TYPES.keySet();

	public WrapVariableInjector(InjectionInfo info) {
		super(info, "@WrapVariable");
	}

	@Override
	protected void inject(Target target, InjectionNode node) {
		checkTargetModifiers(target, false);
		AbstractInsnNode targetNode = node.getCurrentTarget();
		Type type = getType(targetNode);
		assertValidTarget(target, type);

		InjectorData handler = new InjectorData(target, "variable wrapper");
		Type returnType = type == OBJECT_TYPE ? this.returnType : type;
		validateParams(handler, returnType, returnType);

		InsnList addedInsns = new InsnList();
		if (!this.isStatic) {
			// instance methods take self as a secret first parameter
			addedInsns.add(new VarInsnNode(Opcodes.ALOAD, 0));
			// move self to first slot
			if (type.getSize() == 2) { // longs and doubles take up 2 slots
				addedInsns.add(new InsnNode(Opcodes.DUP_X2));
				addedInsns.add(new InsnNode(Opcodes.POP));
			} else {
				addedInsns.add(new InsnNode(Opcodes.SWAP));
			}
		}

		if (handler.captureTargetArgs > 0) {
			Extension stack = target.extendStack();
			pushArgs(target.arguments, addedInsns, target.getArgIndices(), 0, handler.captureTargetArgs, stack);
			stack.apply();
		}

		invokeHandler(addedInsns);
		target.insns.insert(targetNode, addedInsns);
	}

	protected void assertValidTarget(Target target, Type type) {
		if (type == null) {
			throw new InvalidInjectionException(
					this.info,
					"@WrapVariable is targeting an invalid instruction in " + target + " in " + this
			);
		}
	}

	protected Type getType(AbstractInsnNode node) {
		return OPCODE_TYPES.get(node.getOpcode());
	}
}

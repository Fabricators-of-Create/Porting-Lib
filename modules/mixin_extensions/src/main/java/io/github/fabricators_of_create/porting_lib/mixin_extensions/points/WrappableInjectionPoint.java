package io.github.fabricators_of_create.porting_lib.mixin_extensions.points;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable.WrapVariableInjector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.InjectionPoint.AtCode;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

/**
 * Custom injection point that can target specific wrappable opcodes.<br>
 * Specified as "PORTING_LIB:WRAPPABLE" in an @At<br>
 * Can specify which opcodes to target with args: args = "opcodes=ILOAD,I2F"<br>
 * For the supported list, see {@link WrapVariableInjector#OPCODE_TYPES}
 */
@AtCode("WRAPPABLE")
public class WrappableInjectionPoint extends InjectionPoint {
	public static final Map<String, Integer> NAME_TO_OPCODE = new HashMap<>();

	static {
		try {
			boolean foundCodes = false;
			for (Field field : Opcodes.class.getFields()) {
				foundCodes |= field.getName().equals("NOP"); // first opcode field
				if (foundCodes) {
					NAME_TO_OPCODE.put(field.getName(), field.getInt(null));
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private final int ordinal;
	private final Set<Integer> opcodes;

	public WrappableInjectionPoint(InjectionPointData data) {
		super(data);
		this.ordinal = data.getOrdinal();
		this.opcodes = parseOpcodes(data);
	}

	private Set<Integer> parseOpcodes(InjectionPointData data) {
		String opcodesStr = data.get("opcodes", "");
		if (opcodesStr.isEmpty()) {
			return WrapVariableInjector.ALLOWED_OPCODES;
		}
		String[] split = opcodesStr.split(",");
		Set<Integer> opcodes = new HashSet<>();
		for (String opcodeName : split) {
			Integer opcode = NAME_TO_OPCODE.get(opcodeName);
			if (opcode != null) {
				opcodes.add(opcode);
			} else {
				throw new IllegalArgumentException("Unknown/unsupported opcode: " + opcodeName);
			}
		}
		return opcodes;
	}

	@Override
	public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
		boolean found = false;
		int ordinal = 0;
		for (AbstractInsnNode insn : insns) {
			if (opcodes.contains(insn.getOpcode())) {
				if (ordinalMatches(ordinal)) {
					nodes.add(insn);
					found = true;
				}
				ordinal++;
			}
		}
		return found;
	}

	private boolean ordinalMatches(int ordinal) {
		return this.ordinal == -1 || this.ordinal == ordinal;
	}
}

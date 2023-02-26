package io.github.fabricators_of_create.porting_lib;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.chocohead.mm.api.ClassTinkerers;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class PortingLibER implements Runnable {
	public static String mapM(String name) {
		int firstDot = name.indexOf('.');
		// not found          // last character
		if (firstDot == -1 || firstDot == name.length() - 1) {
			throw new IllegalStateException("Invalid method name: " + name);
		}
		String className = name.substring(0, firstDot);
		String methodAndDescriptor = name.substring(firstDot + 1);
		int openParenthesis = methodAndDescriptor.indexOf('(');
		if (openParenthesis == -1) {
			throw new IllegalStateException("descriptor not found: " + methodAndDescriptor);
		}
		String method = methodAndDescriptor.substring(0, openParenthesis);
		String descriptor = methodAndDescriptor.substring(openParenthesis);
		if (descriptor.contains(".")) {
			throw new IllegalStateException("descriptor should be in slash format");
		}
		return FabricLoader.getInstance().getMappingResolver()
				.mapMethodName("intermediary", "net.minecraft." + className, method, descriptor);
	}

	public static String mapF(String name) {
		int firstDot = name.indexOf('.');
		// not found          // last character
		if (firstDot == -1 || firstDot == name.length() - 1) {
			throw new IllegalStateException("Invalid field name: " + name);
		}
		String className = name.substring(0, firstDot);
		String fieldAndDescriptor = name.substring(firstDot + 1);
		int colon = fieldAndDescriptor.indexOf(':');
		if (colon == -1) {
			throw new IllegalStateException("descriptor not found: " + fieldAndDescriptor);
		}
		String fieldName = fieldAndDescriptor.substring(0, colon);
		String descriptor = fieldAndDescriptor.substring(colon + 1);
		if (descriptor.contains(".")) {
			throw new IllegalStateException("descriptor should be in slash format");
		}
		return FabricLoader.getInstance().getMappingResolver()
				.mapFieldName("intermediary", "net.minecraft." + className, fieldName, descriptor);
	}

	@Override
	public void run() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
		String knockbackAttribute = mapF("class_5134.field_23722:Lnet/minecraft/class_1320;");
		String getAttributeMethod = mapM("class_1309.method_26825(Lnet/minecraft/class_1320;)D");
		String attributesClass = remapper.mapClassName("intermediary", "net.minecraft.class_5134").replace('.', '/');
		String attributeClass = remapper.mapClassName("intermediary", "net.minecraft.class_1320").replace('.', '/');
		String attackMethod = mapM("class_1657.method_7324(Lnet/minecraft/class_1297;)V");
		ClassTinkerers.addTransformation(remapper.mapClassName("intermediary", "net.minecraft.class_1657"), classNode -> {
			classNode.methods.forEach(methodNode -> {
				if(methodNode.name.equals(attackMethod)) {
					int instPos = 0, istore = 0, iload = 0;
					for (AbstractInsnNode insnNode : methodNode.instructions) {
						if (insnNode.getOpcode() == Opcodes.ILOAD && insnNode instanceof VarInsnNode node) {
							if (node.var == 7) {
								if (iload == 1) {
									methodNode.instructions.insert(insnNode, listOf(new InsnNode(Opcodes.FCONST_0), new InsnNode(Opcodes.FCMPL)));
								}
								methodNode.instructions.set(insnNode, new VarInsnNode(Opcodes.FLOAD, 7));
								iload++;
							}
						} else if (insnNode.getOpcode() == Opcodes.ISTORE && insnNode instanceof VarInsnNode node) {
							if (node.var == 7) {
								if (istore == 0) {
									InsnList list = new InsnList();
									list.add(new VarInsnNode(Opcodes.ALOAD, 0));
									list.add(new FieldInsnNode(Opcodes.GETSTATIC, attributesClass, knockbackAttribute, "L" + attributeClass +";"));
									list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, remapper.mapClassName("intermediary", "net.minecraft.class_1657").replace('.', '/'), getAttributeMethod, "(L" + attributeClass + ";)D"));
									list.add(new InsnNode(Opcodes.D2F));
									list.add(new VarInsnNode(Opcodes.FSTORE, 7));
									methodNode.instructions.insert(insnNode, list);
									methodNode.instructions.remove(insnNode);
									methodNode.instructions.remove(methodNode.instructions.get(instPos - 1));
								} else
									methodNode.instructions.set(insnNode, new VarInsnNode(Opcodes.FSTORE, 7));
								istore++;
							}
						} else if (insnNode.getOpcode() == Opcodes.IINC) {
							methodNode.instructions.insertBefore(insnNode, listOf(
									new VarInsnNode(Opcodes.FLOAD, 7),
									new InsnNode(Opcodes.FCONST_1),
									new InsnNode(Opcodes.FADD),
									new VarInsnNode(Opcodes.FSTORE, 7)));
							methodNode.instructions.remove(insnNode);
						} else if (insnNode.getOpcode() == Opcodes.I2F) {
							methodNode.instructions.remove(insnNode);
						}
						instPos++;
					}
					int currentPos = methodNode.instructions.indexOf(findFirstInstructionAfter(methodNode, Opcodes.IADD, 0));
					methodNode.instructions.set(methodNode.instructions.get(currentPos), new InsnNode(Opcodes.FADD));
					methodNode.instructions.insertBefore(methodNode.instructions.get(currentPos), new InsnNode(Opcodes.I2F));
					methodNode.localVariables.get(8).desc = "F";
				}
			});
		});
	}

	static AbstractInsnNode findFirstInstructionAfter(MethodNode method, int opCode, int startIndex) {
		for (int i = Math.max(0, startIndex); i < method.instructions.size(); i++) {
			AbstractInsnNode ain = method.instructions.get(i);
			if (ain.getOpcode() == opCode) {
				return ain;
			}
		}
		return null;
	}

	static InsnList listOf(AbstractInsnNode... nodes) {
		InsnList list = new InsnList();
		for (AbstractInsnNode node : nodes)
			list.add(node);
		return list;
	}
}

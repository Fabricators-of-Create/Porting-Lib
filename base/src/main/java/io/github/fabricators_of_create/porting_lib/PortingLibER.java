package io.github.fabricators_of_create.porting_lib;

import com.google.common.collect.ImmutableList;

import io.github.fabricators_of_create.porting_lib.asm.ASMUtils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.chocohead.mm.api.ClassTinkerers;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import java.util.List;

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

		PATCHES.forEach(s -> ClassTinkerers.addTransformation(s, PortingLibER::replaceMethodClass));
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

	private static List<String> mapClasses(String ...classes) {
		ImmutableList.Builder<String> mappedClasses = ImmutableList.builder();
		for (String clazz : classes)
			mappedClasses.add(ASMUtils.mapC(clazz));
		return mappedClasses.build();
	}

	private static List<String> PATCHES = mapClasses(
			"class_6329",
    		"class_3138",
			"class_1299",
			"class_1419",
			"class_7110",
			"class_1505",
			"class_1564$class_1567",
			"class_1628",
			"class_4985",
			"class_1642",
			"class_1641",
			"class_4274",
			"class_1646",
			"class_3765",
			"class_1948",
			"class_3769",
			"class_2910",
			"class_3366$class_3384",
			"class_3409$class_3410",
			"class_3447",
			"class_3471$class_3480",
			"class_3499"
	);

	private static String spawnMethod = ASMUtils.mapM("class_1308.method_5943(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;Lnet/minecraft/class_2487;)Lnet/minecraft/class_1315;");
	private static String spawnMethodDesc = "(L" + mapClass("class_5425") + ";L" + mapClass("class_1266") + ";L" + mapClass("class_3730") + ";L" + mapClass("class_1315") + ";L" + mapClass("class_2487") + ";)L" + mapClass("class_1315") + ";";

	private static String PATCHED_DESC = "(L" + mapClass("class_1308") + ";L" + mapClass("class_5425") + ";L" + mapClass("class_1266") + ";L" + mapClass("class_3730") + ";L" + mapClass("class_1315") + ";L" + mapClass("class_2487") + ";)L" + mapClass("class_1315") + ";";
	private static String mapClass(String name) {
		return ASMUtils.mapC(name).replace('.', '/');
	}

	private static MethodInsnNode finalizeSpawnNode() {
		return new MethodInsnNode(
				Opcodes.INVOKESTATIC,
				"io/github/fabricators_of_create/porting_lib/util/PortingHooks",
				"onFinalizeSpawn",
				PATCHED_DESC,
				false);
	}

	private static boolean contains(List<String> list, String target) {
		for (String s : list) {
			if (s.equals(target)) return true;
		}
		return false;
	}

	private static MethodInsnNode search(String className, MethodInsnNode node, List<String> replacements) {
		if (contains(replacements, className.replace('/', '.')) && node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.name.equals(spawnMethod) && node.desc.equals(spawnMethodDesc)) {
			return finalizeSpawnNode();
		}
		return null;
	}

	private static void replaceMethodClass(ClassNode classNode) {
		var methods = classNode.methods;
		for(var i = 0; i < methods.size(); i++){
			var instr = methods.get(i).instructions;
			for(var ix = 0; ix < instr.size(); ix++){
				var node = instr.get(ix);
				if (node instanceof MethodInsnNode methodInsnNode) {
					var temp = search(classNode.name, methodInsnNode, PATCHES);
					if (temp != null) {
						instr.set(node, temp);
					}
				}
			}
		}
	}
}

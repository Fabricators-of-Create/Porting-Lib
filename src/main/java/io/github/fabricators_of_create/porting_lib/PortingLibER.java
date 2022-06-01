package io.github.fabricators_of_create.porting_lib;

import com.chocohead.mm.api.ClassTinkerers;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class PortingLibER implements Runnable {
	@Override
	public void run() {
		MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
		ClassTinkerers.addTransformation(remapper.mapClassName("intermediary", "net.minecraft.class_793"), classNode -> {
			classNode.methods.forEach(methodNode -> {
				if(methodNode.name.equals("getMaterials")) { // TODO: FIX I have no fucking clue how to debug this
					methodNode.instructions.insertBefore(findFirstInstructionAfter(methodNode, Opcodes.INVOKESTATIC, 2), listOf(
							new VarInsnNode(Opcodes.ALOAD, 0),
							new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/block/model/BlockModel", "data", "io/github/fabricators_of_create/porting_lib.model/BlockModelConfiguration"),
							new JumpInsnNode(Opcodes.IFNULL, new LabelNode()),
							new VarInsnNode(Opcodes.ALOAD, 4),
							new VarInsnNode(Opcodes.ALOAD, 0),
							new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/block/model/BlockModel", "data", "io/github/fabricators_of_create/porting_lib.model/BlockModelConfiguration"),
							new VarInsnNode(Opcodes.ALOAD, 1),
							new VarInsnNode(Opcodes.ALOAD, 2),
							new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "io/github/fabricators_of_create/porting_lib.model/BlockModelConfiguration", "getTextureDependencies", "(Ljava/util/function/Function;Ljava/util/Set;)Ljava/util/Collection;"),
							new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Set", "addAll", "(Ljava/util/Collection;)Z", true)
					));
					System.out.println(methodNode.instructions);
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

package io.github.fabricators_of_create.porting_lib.attributes;

import static io.github.fabricators_of_create.porting_lib.asm.ASMUtils.mapC;
import static io.github.fabricators_of_create.porting_lib.asm.ASMUtils.mapF;
import static io.github.fabricators_of_create.porting_lib.asm.ASMUtils.mapM;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.chocohead.mm.api.ClassTinkerers;

import io.github.fabricators_of_create.porting_lib.asm.ASMUtils;

public class PortingLibAttributesER implements Runnable {
	@Override
	public void run() {
		// Idk how to do this with mixin so asm is my next best bet
		ClassTinkerers.addTransformation(mapC("class_3244"), classNode -> {
			classNode.methods.forEach(methodNode -> {
				if (!methodNode.name.equals(mapM("class_2792.method_12062(Lnet/minecraft/class_2824;)V"))) // ServerGamePacketListenerImpl#handleInteract
					return;
				methodNode.instructions.forEach(abstractInsnNode -> {
					if (abstractInsnNode instanceof MethodInsnNode node) {
						if (node.name.equals(mapM("class_1297.method_33571()Lnet/minecraft/class_243;"))) {
							methodNode.instructions.insertBefore(node, ASMUtils.listOf(new VarInsnNode(Opcodes.ALOAD, 3), new LdcInsnNode(3.0)));
							node.name = "canReach";
							node.desc = "(L" + mapC("class_1297").replace('.', '/') + ";D)Z";
							node.owner = mapC("class_3222").replace('.', '/');
						}
						if (node.name.equals(mapM("class_238.method_49271(Lnet/minecraft/class_243;)D"))) {
							methodNode.instructions.remove(node);
						}
					}
					if (abstractInsnNode.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) abstractInsnNode).var == 4)
						methodNode.instructions.remove(abstractInsnNode);
					if (abstractInsnNode.getOpcode() == Opcodes.IFGE)
						((JumpInsnNode)abstractInsnNode).setOpcode(Opcodes.IFEQ);
				});
				methodNode.instructions.remove(ASMUtils.findFirstInstruction(methodNode, Opcodes.GETSTATIC));
				methodNode.instructions.remove(ASMUtils.findFirstInstruction(methodNode, Opcodes.DCMPG));
			});
		});

		String gameRendererClass = mapC("class_757").replace('.', '/');
		String minecraftClass = mapC("class_310").replace('.', '/');
		String localPlayerClass = mapC("class_746").replace('.', '/');
		ClassTinkerers.addTransformation(mapC("class_757"), classNode -> {
			classNode.methods.forEach(methodNode -> {
				if (!methodNode.name.equals(mapM("class_757.method_3190(F)V"))) // GameRenderer#pick
					return;
				var inst = ASMUtils.listOf(new VarInsnNode(Opcodes.DLOAD, 3),
						new VarInsnNode(Opcodes.DLOAD, 8),
						new VarInsnNode(Opcodes.ALOAD, 0),
						new FieldInsnNode(Opcodes.GETFIELD, gameRendererClass, mapF("class_757.field_4015:Lnet/minecraft/class_310;"), "L" + minecraftClass + ";"),
						new FieldInsnNode(Opcodes.GETFIELD, minecraftClass, mapF("class_310.field_1724:Lnet/minecraft/class_746;"), "L" + localPlayerClass + ";"),
						new MethodInsnNode(Opcodes.INVOKEVIRTUAL, localPlayerClass, "getEntityReach", "()D"),
						new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Math", "max", "(DD)D"),
						new InsnNode(Opcodes.DUP2),
						new VarInsnNode(Opcodes.DSTORE, 8),
						new VarInsnNode(Opcodes.DSTORE, 3)
				);
				methodNode.instructions.insertBefore(ASMUtils.findFirstInstruction(methodNode, Opcodes.DMUL).getPrevious().getPrevious(), inst);
			});
		});
	}
}

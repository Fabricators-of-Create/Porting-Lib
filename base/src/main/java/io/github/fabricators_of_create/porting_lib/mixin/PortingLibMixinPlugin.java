package io.github.fabricators_of_create.porting_lib.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableList;

import io.github.fabricators_of_create.porting_lib.asm.ASMUtils;
import net.fabricmc.loader.api.FabricLoader;

public class PortingLibMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.contains("ModelBlockRenderer") && FabricLoader.getInstance().isModLoaded("canvas"))
			return false;
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

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

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		PATCHES.forEach(s -> {
			if (s.equals(targetClassName)) {
				replaceMethodClass(targetClass);
			}
		});
	}

	private static String spawnMethod = ASMUtils.mapM("class_1308.method_5943(Lnet/minecraft/class_5425;Lnet/minecraft/class_1266;Lnet/minecraft/class_3730;Lnet/minecraft/class_1315;Lnet/minecraft/class_2487;)Lnet/minecraft/class_1315;");
	private static String spawnMethodDesc = "(L" + mapClass("class_5425") + ";L" + mapClass("class_1266") + ";L" + mapClass("class_3730") + ";L" + mapClass("class_1315") + ";L" + mapClass("class_2487") + ";)L" + mapClass("class_1315") + ";";

	private static String PATCHED_DESC = "(L" + mapClass("class_1308") + ";L" + mapClass("class_5425") + ";L" + mapClass("class_1266") + ";L" + mapClass("class_3730") + ";L" + mapClass("class_1315") + ";L" + mapClass("class_2487") + ";)L" + mapClass("class_1315") + ";";
	private static String mapClass(String name) {
		return ASMUtils.mapC(name).replace('.', '/');
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

	private static MethodInsnNode search(String className, MethodInsnNode node, List<String> replacements) {
		if (contains(replacements, className.replace('/', '.')) && node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.name.equals(spawnMethod) && node.desc.equals(spawnMethodDesc)) {
			return finalizeSpawnNode();
		}
		return null;
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

}

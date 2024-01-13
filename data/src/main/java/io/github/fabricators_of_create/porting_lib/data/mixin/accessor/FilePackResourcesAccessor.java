package io.github.fabricators_of_create.porting_lib.data.mixin.accessor;

import net.minecraft.server.packs.FilePackResources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FilePackResources.class)
public interface FilePackResourcesAccessor {
	@Invoker("<init>")
	static FilePackResources callInit(String name, FilePackResources.SharedZipFileAccess zipFile, boolean builtin, String prefix) {
		throw new AssertionError();
	}
}

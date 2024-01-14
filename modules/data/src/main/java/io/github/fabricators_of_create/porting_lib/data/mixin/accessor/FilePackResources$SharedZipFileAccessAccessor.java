package io.github.fabricators_of_create.porting_lib.data.mixin.accessor;

import net.minecraft.server.packs.FilePackResources;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.File;

@Mixin(FilePackResources.SharedZipFileAccess.class)
public interface FilePackResources$SharedZipFileAccessAccessor {
	@Invoker("<init>")
	static FilePackResources.SharedZipFileAccess callInit(File file) {
		throw new AssertionError();
	}
}

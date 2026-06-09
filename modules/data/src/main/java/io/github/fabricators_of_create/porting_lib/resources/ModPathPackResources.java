package io.github.fabricators_of_create.porting_lib.resources;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class ModPathPackResources extends PathPackResources {

    private final ModContainer mf;

    public ModPathPackResources(ModContainer mf) {
        super(mf.getMetadata().getName(), false, mf.getRootPath());
        this.mf = mf;
    }

    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        try {
            Path path = this.resolve(paths);
            return !Files.exists(path) ? null : IoSupplier.create(path);
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    protected Path resolve(String... paths) {
        String path = String.join("/", paths);
        return mf.findPath(path).orElseThrow(() -> new NoSuchElementException("Path " + path + " not found"));
    }

}
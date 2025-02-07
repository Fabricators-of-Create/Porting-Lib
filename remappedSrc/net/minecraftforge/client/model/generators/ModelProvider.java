/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client.model.generators;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;

public abstract class ModelProvider<T extends ModelBuilder<T>> implements DataProvider {

    public static final String BLOCK_FOLDER = "block";
    public static final String ITEM_FOLDER = "item";

    protected static final ResourceType TEXTURE = new ResourceType(net.minecraft.resource.ResourceType.CLIENT_RESOURCES, ".png", "textures");
    protected static final ResourceType MODEL = new ResourceType(net.minecraft.resource.ResourceType.CLIENT_RESOURCES, ".json", "models");
    protected static final ResourceType MODEL_WITH_EXTENSION = new ResourceType(net.minecraft.resource.ResourceType.CLIENT_RESOURCES, "", "models");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final String modid;
    protected final String folder;
    protected final Function<Identifier, T> factory;
    @VisibleForTesting
    public final Map<Identifier, T> generatedModels = new HashMap<>();
    @VisibleForTesting
    public final ExistingFileHelper existingFileHelper;

    protected abstract void registerModels();

    public ModelProvider(DataGenerator generator, String modid, String folder, Function<Identifier, T> factory, ExistingFileHelper existingFileHelper) {
        Preconditions.checkNotNull(generator);
        this.generator = generator;
        Preconditions.checkNotNull(modid);
        this.modid = modid;
        Preconditions.checkNotNull(folder);
        this.folder = folder;
        Preconditions.checkNotNull(factory);
        this.factory = factory;
        Preconditions.checkNotNull(existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    public ModelProvider(DataGenerator generator, String modid, String folder, BiFunction<Identifier, ExistingFileHelper, T> builderFromModId, ExistingFileHelper existingFileHelper) {
        this(generator, modid, folder, loc->builderFromModId.apply(loc, existingFileHelper), existingFileHelper);
    }

    public T getBuilder(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        Identifier outputLoc = extendWithFolder(path.contains(":") ? new Identifier(path) : new Identifier(modid, path));
        this.existingFileHelper.trackGenerated(outputLoc, MODEL);
        return generatedModels.computeIfAbsent(outputLoc, factory);
    }

    private Identifier extendWithFolder(Identifier rl) {
        if (rl.getPath().contains("/")) {
            return rl;
        }
        return new Identifier(rl.getNamespace(), folder + "/" + rl.getPath());
    }

    public Identifier modLoc(String name) {
        return new Identifier(modid, name);
    }

    public Identifier mcLoc(String name) {
        return new Identifier(name);
    }

    public T withExistingParent(String name, String parent) {
        return withExistingParent(name, mcLoc(parent));
    }

    public T withExistingParent(String name, Identifier parent) {
        return getBuilder(name).parent(getExistingFile(parent));
    }

    public T cube(String name, Identifier down, Identifier up, Identifier north, Identifier south, Identifier east, Identifier west) {
        return withExistingParent(name, "cube")
                .texture("down", down)
                .texture("up", up)
                .texture("north", north)
                .texture("south", south)
                .texture("east", east)
                .texture("west", west);
    }

    private T singleTexture(String name, String parent, Identifier texture) {
        return singleTexture(name, mcLoc(parent), texture);
    }

    public T singleTexture(String name, Identifier parent, Identifier texture) {
        return singleTexture(name, parent, "texture", texture);
    }

    private T singleTexture(String name, String parent, String textureKey, Identifier texture) {
        return singleTexture(name, mcLoc(parent), textureKey, texture);
    }

    public T singleTexture(String name, Identifier parent, String textureKey, Identifier texture) {
        return withExistingParent(name, parent)
                .texture(textureKey, texture);
    }

    public T cubeAll(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/cube_all", "all", texture);
    }

    public T cubeTop(String name, Identifier side, Identifier top) {
        return withExistingParent(name, BLOCK_FOLDER + "/cube_top")
                .texture("side", side)
                .texture("top", top);
    }

    private T sideBottomTop(String name, String parent, Identifier side, Identifier bottom, Identifier top) {
        return withExistingParent(name, parent)
                .texture("side", side)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public T cubeBottomTop(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/cube_bottom_top", side, bottom, top);
    }

    public T cubeColumn(String name, Identifier side, Identifier end) {
        return withExistingParent(name, BLOCK_FOLDER + "/cube_column")
                .texture("side", side)
                .texture("end", end);
    }

    public T cubeColumnHorizontal(String name, Identifier side, Identifier end) {
        return withExistingParent(name, BLOCK_FOLDER + "/cube_column_horizontal")
                .texture("side", side)
                .texture("end", end);
    }

    public T orientableVertical(String name, Identifier side, Identifier front) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable_vertical")
                .texture("side", side)
                .texture("front", front);
    }

    public T orientableWithBottom(String name, Identifier side, Identifier front, Identifier bottom, Identifier top) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable_with_bottom")
                .texture("side", side)
                .texture("front", front)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public T orientable(String name, Identifier side, Identifier front, Identifier top) {
        return withExistingParent(name, BLOCK_FOLDER + "/orientable")
                .texture("side", side)
                .texture("front", front)
                .texture("top", top);
    }

    public T crop(String name, Identifier crop) {
        return singleTexture(name, BLOCK_FOLDER + "/crop", "crop", crop);
    }

    public T cross(String name, Identifier cross) {
        return singleTexture(name, BLOCK_FOLDER + "/cross", "cross", cross);
    }

    public T stairs(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/stairs", side, bottom, top);
    }

    public T stairsOuter(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/outer_stairs", side, bottom, top);
    }

    public T stairsInner(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/inner_stairs", side, bottom, top);
    }

    public T slab(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/slab", side, bottom, top);
    }

    public T slabTop(String name, Identifier side, Identifier bottom, Identifier top) {
        return sideBottomTop(name, BLOCK_FOLDER + "/slab_top", side, bottom, top);
    }

    public T fencePost(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_post", texture);
    }

    public T fenceSide(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_side", texture);
    }

    public T fenceInventory(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/fence_inventory", texture);
    }

    public T fenceGate(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate", texture);
    }

    public T fenceGateOpen(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_open", texture);
    }

    public T fenceGateWall(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall", texture);
    }

    public T fenceGateWallOpen(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_fence_gate_wall_open", texture);
    }

    public T wallPost(String name, Identifier wall) {
        return singleTexture(name, BLOCK_FOLDER + "/template_wall_post", "wall", wall);
    }

    public T wallSide(String name, Identifier wall) {
        return singleTexture(name, BLOCK_FOLDER + "/template_wall_side", "wall", wall);
    }

    public T wallSideTall(String name, Identifier wall) {
        return singleTexture(name, BLOCK_FOLDER + "/template_wall_side_tall", "wall", wall);
    }

    public T wallInventory(String name, Identifier wall) {
        return singleTexture(name, BLOCK_FOLDER + "/wall_inventory", "wall", wall);
    }

    private T pane(String name, String parent, Identifier pane, Identifier edge) {
        return withExistingParent(name, BLOCK_FOLDER + "/" + parent)
                .texture("pane", pane)
                .texture("edge", edge);
    }

    public T panePost(String name, Identifier pane, Identifier edge) {
        return pane(name, "template_glass_pane_post", pane, edge);
    }

    public T paneSide(String name, Identifier pane, Identifier edge) {
        return pane(name, "template_glass_pane_side", pane, edge);
    }

    public T paneSideAlt(String name, Identifier pane, Identifier edge) {
        return pane(name, "template_glass_pane_side_alt", pane, edge);
    }

    public T paneNoSide(String name, Identifier pane) {
        return singleTexture(name, BLOCK_FOLDER + "/template_glass_pane_noside", "pane", pane);
    }

    public T paneNoSideAlt(String name, Identifier pane) {
        return singleTexture(name, BLOCK_FOLDER + "/template_glass_pane_noside_alt", "pane", pane);
    }

    private T door(String name, String model, Identifier bottom, Identifier top) {
        return withExistingParent(name, BLOCK_FOLDER + "/" + model)
                .texture("bottom", bottom)
                .texture("top", top);
    }

    public T doorBottomLeft(String name, Identifier bottom, Identifier top) {
        return door(name, "door_bottom", bottom, top);
    }

    public T doorBottomRight(String name, Identifier bottom, Identifier top) {
        return door(name, "door_bottom_rh", bottom, top);
    }

    public T doorTopLeft(String name, Identifier bottom, Identifier top) {
        return door(name, "door_top", bottom, top);
    }

    public T doorTopRight(String name, Identifier bottom, Identifier top) {
        return door(name, "door_top_rh", bottom, top);
    }

    public T trapdoorBottom(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_bottom", texture);
    }

    public T trapdoorTop(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_top", texture);
    }

    public T trapdoorOpen(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_trapdoor_open", texture);
    }

    public T trapdoorOrientableBottom(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_bottom", texture);
    }

    public T trapdoorOrientableTop(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_top", texture);
    }

    public T trapdoorOrientableOpen(String name, Identifier texture) {
        return singleTexture(name, BLOCK_FOLDER + "/template_orientable_trapdoor_open", texture);
    }

    public T torch(String name, Identifier torch) {
        return singleTexture(name, BLOCK_FOLDER + "/template_torch", "torch", torch);
    }

    public T torchWall(String name, Identifier torch) {
        return singleTexture(name, BLOCK_FOLDER + "/template_torch_wall", "torch", torch);
    }

    public T carpet(String name, Identifier wool) {
        return singleTexture(name, BLOCK_FOLDER + "/carpet", "wool", wool);
    }

    /**
     * Gets a model builder that's not directly saved to disk. Meant for use in custom model loaders.
     */
    public T nested()
    {
        return factory.apply(new Identifier("dummy:dummy"));
    }

    public ModelFile.ExistingModelFile getExistingFile(Identifier path) {
        ModelFile.ExistingModelFile ret = new ModelFile.ExistingModelFile(extendWithFolder(path), existingFileHelper);
        ret.assertExistence();
        return ret;
    }

    protected void clear() {
        generatedModels.clear();
    }

    @Override
    public void run(DataCache cache) throws IOException {
        clear();
        registerModels();
        generateAll(cache);
    }

    protected void generateAll(DataCache cache) {
        for (T model : generatedModels.values()) {
            Path target = getPath(model);
            try {
                DataProvider.writeToPath(GSON, cache, model.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path getPath(T model) {
        Identifier loc = model.getLocation();
        return generator.getOutput().resolve("assets/" + loc.getNamespace() + "/models/" + loc.getPath() + ".json");
    }
}

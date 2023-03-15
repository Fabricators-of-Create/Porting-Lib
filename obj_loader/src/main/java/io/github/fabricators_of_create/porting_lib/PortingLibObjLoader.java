package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PortingLibObjLoader implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterGeometryLoadersCallback.EVENT.register(loaders -> loaders.put(PortingConstants.id("obj"), ObjLoader.INSTANCE));
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ObjLoader.INSTANCE);
//		ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> loadModel(resourceManager, resourceId));
//		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> manager.listResources("models/misc", resourceLocation -> {
//			if (resourceLocation.getPath().endsWith(".json")) {
//				manager.getResource(resourceLocation).ifPresent(resource -> {
//					try {
//					JsonObject jsonObject = Streams.parse(new JsonReader(new InputStreamReader(resource.open(), Charsets.UTF_8))).getAsJsonObject();
//					if (jsonObject.has(PortingConstants.ID + ":" + "obj_marker")) {
//						out.accept(resourceLocation);
//					}
//					} catch (IOException | NoSuchElementException e) {
//						e.fillInStackTrace();
//					}
//				});
//			}
//			return true;
//		}));
	}

//	@Nullable
//	public static UnbakedModel loadModel(ResourceManager resourceManager, ResourceLocation modelLocation) {
//		ObjLoader.INSTANCE.setManager(resourceManager);
//		if (!modelLocation.getPath().endsWith(".json"))
//			return null;
//		Resource resource = resourceManager.getResource(new ResourceLocation(modelLocation.getNamespace(), modelLocation.getPath())).orElse(null);
//		if (resource != null) {
//			try {
//				JsonObject jsonObject = Streams.parse(new JsonReader(new InputStreamReader(resource.open(), Charsets.UTF_8))).getAsJsonObject();
//				if (jsonObject.has(PortingConstants.ID + ":" + "obj_marker")) {
//					ResourceLocation objLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
//					return ObjLoader.INSTANCE.loadModel(resourceManager.getResource(objLocation).orElseThrow(), new ObjModel.ModelSettings(objLocation, true, true, true, true, null));
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
}

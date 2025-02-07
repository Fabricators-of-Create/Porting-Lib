package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.client.ModelsBakedCallback;
import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientBlockApiCache;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidVariantFluidAttributesHandler;
import io.github.fabricators_of_create.porting_lib.util.LogicalSidedProvider;
import io.github.fabricators_of_create.porting_lib.util.NetworkUtil;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingLibClient implements ClientModInitializer {
	private final Logger LOGGER = LoggerFactory.getLogger("porting_lib_client");

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
		ExtraSpawnDataEntity.initClientNetworking();
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().put(part.getId(), part);
				}
			}
		});
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().remove(part.getId());
				}
			}
		});
		ClientBlockApiCache.init();
		ModelsBakedCallback.EVENT.register((manager, models, loader) -> {
			Registry.FLUID.forEach(fluid -> {
				ClientHooks.registerFluidVariantsFromAttributes(fluid, fluid.getAttributes());
				if (FluidVariantAttributes.getHandler(fluid) == null)
					FluidVariantAttributes.register(fluid, new FluidVariantFluidAttributesHandler(fluid.getAttributes()));
			});
		});
		ClientPlayNetworking.registerGlobalReceiver(NetworkUtil.OPEN_ID, (client, handler, buf, responseSender) -> {
			int typeId = buf.readVarInt();
			int syncId = buf.readVarInt();
			Text title = buf.readText();
			PacketByteBuf extraData = new PacketByteBuf(Unpooled.wrappedBuffer(buf.readByteArray(32600)));
			// Retain the buf since we must open the screen handler with it's extra modded data on the client thread
			// The buf will be released after the screen is opened
			buf.retain();

			client.execute(() -> openScreen(typeId, syncId, title, extraData));
		});
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> LogicalSidedProvider.setClient(() -> client));
	}

	private void openScreen(int typeId, int syncId, Text title, PacketByteBuf buf) {
		try {
			ScreenHandlerType<?> type = Registry.SCREEN_HANDLER.get(typeId);

			if (type == null) {
				LOGGER.warn("Unknown screen handler ID: {}", typeId);
				return;
			}

			if (!(type instanceof ExtendedScreenHandlerType<?>)) {
				LOGGER.warn("Received extended opening packet for non-extended screen handler {}", typeId);
				return;
			}

			ScreenHandler c = ((ExtendedScreenHandlerType)type).create(syncId, MinecraftClient.getInstance().player.getInventory(), buf);
			@SuppressWarnings("unchecked")
			Screen s = ((HandledScreens.Provider<ScreenHandler, ?>)HandledScreens.getProvider(type)).create(c, MinecraftClient.getInstance().player.getInventory(), title);
			MinecraftClient.getInstance().player.currentScreenHandler = ((ScreenHandlerProvider<?>)s).getScreenHandler();
			MinecraftClient.getInstance().setScreen(s);
		} finally {
			buf.release(); // Release the buf
		}
	}
}

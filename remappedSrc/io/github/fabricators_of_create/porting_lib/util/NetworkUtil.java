package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.ServerPlayerAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class NetworkUtil {
	public static final Identifier OPEN_ID = PortingLib.id("open_screen");

	public static void openGui(ServerPlayerEntity player, NamedScreenHandlerFactory containerProvider, BlockPos pos) {
		openGui(player, containerProvider, buf -> buf.writeBlockPos(pos));
	}

	public static void openGui(ServerPlayerEntity player, NamedScreenHandlerFactory factory, Consumer<PacketByteBuf> extraDataWriter) {
		player.closeScreenHandler();
		((ServerPlayerAccessor)player).callNextContainerCounter();
		int openContainerId = ((ServerPlayerAccessor)player).getContainerCounter();

		PacketByteBuf extraData = new PacketByteBuf(Unpooled.buffer());
		extraDataWriter.accept(extraData);
		extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		ScreenHandler menu = factory.createMenu(openContainerId, player.getInventory(), player);
		buf.writeVarInt(Registry.SCREEN_HANDLER.getRawId(menu.getType()));
		buf.writeVarInt(openContainerId);
		buf.writeText(factory.getDisplayName());
		buf.writeVarInt(extraData.readableBytes());
		buf.writeBytes(extraData);

		ServerPlayNetworking.send(player, OPEN_ID, buf);

		player.currentScreenHandler = menu;
		((ServerPlayerAccessor)player).callInitMenu(menu);
	}
}

package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.ServerPlayerAccessor;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class NetworkHooks {
	public static final ResourceLocation OPEN_ID = PortingLib.id("open_screen");

	/**
	 * Request to open a GUI on the client, from the server
	 *
	 * @param player The player to open the GUI for
	 * @param containerSupplier A supplier creating a menu instance on the server.
	 */
	public static void openScreen(ServerPlayer player, MenuProvider containerSupplier) {
		openScreen(player, containerSupplier, buf -> {});
	}

	/**
	 * Request to open a GUI on the client, from the server
	 *
	 * @param player The player to open the GUI for
	 * @param containerProvider A supplier creating a menu instance on the server.
	 * @param pos A block pos, which will be encoded into the auxillary data for this request
	 */
	public static void openScreen(ServerPlayer player, MenuProvider containerProvider, BlockPos pos) {
		openScreen(player, containerProvider, buf -> buf.writeBlockPos(pos));
	}

	/**
	 * Request to open a GUI on the client, from the server
	 *
	 * @param player The player to open the GUI for
	 * @param factory A supplier creating a menu instance on the server.
	 * @param extraDataWriter Consumer to write any additional data the GUI needs
	 */
	public static void openScreen(ServerPlayer player, MenuProvider factory, Consumer<FriendlyByteBuf> extraDataWriter) {
		player.doCloseContainer();
		((ServerPlayerAccessor)player).callNextContainerCounter();
		int openContainerId = ((ServerPlayerAccessor)player).getContainerCounter();

		FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
		extraDataWriter.accept(extraData);
		extraData.readerIndex(0); // reset to beginning in case modders read for whatever reason
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		AbstractContainerMenu menu = factory.createMenu(openContainerId, player.getInventory(), player);
		buf.writeVarInt(BuiltInRegistries.MENU.getId(menu.getType()));
		buf.writeVarInt(openContainerId);
		buf.writeComponent(factory.getDisplayName());
		buf.writeVarInt(extraData.readableBytes());
		buf.writeBytes(extraData);

		ServerPlayNetworking.send(player, OPEN_ID, buf);

		player.containerMenu = menu;
		((ServerPlayerAccessor)player).callInitMenu(menu);
	}
}

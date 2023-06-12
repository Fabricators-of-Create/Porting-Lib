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

	public static void openScreen(ServerPlayer player, MenuProvider containerProvider, BlockPos pos) {
		openScreen(player, containerProvider, buf -> buf.writeBlockPos(pos));
	}

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

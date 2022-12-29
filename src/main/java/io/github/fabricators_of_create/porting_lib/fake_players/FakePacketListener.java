package io.github.fabricators_of_create.porting_lib.fake_players;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class FakePacketListener extends ServerGamePacketListenerImpl {
	private static final Connection DUMMY_CONNECTION = new FakeConnection(PacketFlow.CLIENTBOUND);

	public FakePacketListener(MinecraftServer server, ServerPlayer player) {
		super(server, DUMMY_CONNECTION, player);
	}

	@Override
	public void tick() {}

	@Override
	public void resetPosition() {}

	@Override
	public void disconnect(Component message) {}

	@Override
	public void handlePlayerInput(ServerboundPlayerInputPacket packet) {}

	@Override
	public void handleMoveVehicle(ServerboundMoveVehiclePacket packet) {}

	@Override
	public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket packet) {}

	@Override
	public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket packet) {}

	@Override
	public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket packet) {}

	@Override
	public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket packet) {}

	@Override
	public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket packet) {}

	@Override
	public void handleSetCommandBlock(ServerboundSetCommandBlockPacket packet) {}

	@Override
	public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket packet) {}

	@Override
	public void handlePickItem(ServerboundPickItemPacket packet) {}

	@Override
	public void handleRenameItem(ServerboundRenameItemPacket packet) {}

	@Override
	public void handleSetBeaconPacket(ServerboundSetBeaconPacket packet) {}

	@Override
	public void handleSetStructureBlock(ServerboundSetStructureBlockPacket packet) {}

	@Override
	public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket packet) {}

	@Override
	public void handleJigsawGenerate(ServerboundJigsawGeneratePacket packet) {}

	@Override
	public void handleSelectTrade(ServerboundSelectTradePacket packet) {}

	@Override
	public void handleEditBook(ServerboundEditBookPacket packet) {}

	@Override
	public void handleEntityTagQuery(ServerboundEntityTagQuery packet) {}

	@Override
	public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery packet) {}

	@Override
	public void handleMovePlayer(ServerboundMovePlayerPacket packet) {}

	@Override
	public void teleport(double x, double y, double z, float yaw, float pitch) {}

	@Override
	public void teleport(double x, double y, double z, float yaw, float pitch, Set<ClientboundPlayerPositionPacket.RelativeArgument> flags) {}

	@Override
	public void handlePlayerAction(ServerboundPlayerActionPacket packet) {}

	@Override
	public void handleUseItemOn(ServerboundUseItemOnPacket packet) {}

	@Override
	public void handleUseItem(ServerboundUseItemPacket packet) {}

	@Override
	public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket packet) {}

	@Override
	public void handleResourcePackResponse(ServerboundResourcePackPacket packet) {}

	@Override
	public void handlePaddleBoat(ServerboundPaddleBoatPacket packet) {}

	@Override
	public void onDisconnect(Component message) {}

	@Override
	public void send(Packet<?> packet) {}

	@Override
	public void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> futureListeners) {}

	@Override
	public void handleSetCarriedItem(ServerboundSetCarriedItemPacket packet) {}

	@Override
	public void handleChat(ServerboundChatPacket packet) {}

	@Override
	public void handleAnimate(ServerboundSwingPacket packet) {}

	@Override
	public void handlePlayerCommand(ServerboundPlayerCommandPacket packet) {}

	@Override
	public void handleInteract(ServerboundInteractPacket packet) {}

	@Override
	public void handleClientCommand(ServerboundClientCommandPacket packet) {}

	@Override
	public void handleContainerClose(ServerboundContainerClosePacket packet) {}

	@Override
	public void handleContainerClick(ServerboundContainerClickPacket packet) {}

	@Override
	public void handlePlaceRecipe(ServerboundPlaceRecipePacket packet) {}

	@Override
	public void handleContainerButtonClick(ServerboundContainerButtonClickPacket packet) {}

	@Override
	public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket packet) {}

	@Override
	public void handleSignUpdate(ServerboundSignUpdatePacket packet) {}

	@Override
	public void handleKeepAlive(ServerboundKeepAlivePacket packet) {}

	@Override
	public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket packet) {}

	@Override
	public void handleClientInformation(ServerboundClientInformationPacket packet) {}

	@Override
	public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {}

	@Override
	public void handleChangeDifficulty(ServerboundChangeDifficultyPacket packet) {}

	@Override
	public void handleLockDifficulty(ServerboundLockDifficultyPacket packet) {}

	// these 2 are not overriden on forge. We won't override them for parity, but they're here for reference.
//
//	@Override
//	public void handlePong(ServerboundPongPacket packet) {}
//
//	@Override
//	public void teleport(double x, double y, double z, float yaw, float pitch, Set<RelativeArgument> relativeSet, boolean dismountVehicle) {}
}

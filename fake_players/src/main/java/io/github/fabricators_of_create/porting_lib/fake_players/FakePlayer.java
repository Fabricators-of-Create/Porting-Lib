package io.github.fabricators_of_create.porting_lib.fake_players;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class FakePlayer extends ServerPlayer {
	public FakePlayer(ServerLevel world, GameProfile gameProfile) {
		super(world.getServer(), world, gameProfile, null);
		this.connection = new FakePacketListener(world.getServer(), this);
	}

	@Override
	public boolean isFakePlayer() {
		return true;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	public boolean canHarmPlayer(Player player) {
		return false;
	}

	@Override
	public void displayClientMessage(Component chatComponent, boolean actionBar) {}

	@Override
	public void awardStat(Stat stat, int amount) {}

	@Override
	public void die(DamageSource source) {}

	@Override
	public void tick() {}

	@Override
	public void updateOptions(ServerboundClientInformationPacket packet) {}
}

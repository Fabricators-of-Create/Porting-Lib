package io.github.fabricators_of_create.porting_lib.fake_players;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FakePlayer extends ServerPlayerEntity {
	public FakePlayer(ServerWorld world, GameProfile gameProfile) {
		super(world.getServer(), world, gameProfile);
		this.networkHandler = new FakePacketListener(world.getServer(), this);
	}

	@Override
	public boolean isFake() {
		return true;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return true;
	}

	@Override
	public boolean shouldDamagePlayer(PlayerEntity player) {
		return false;
	}

	@Override
	public void sendMessage(Text chatComponent, boolean actionBar) {}

	@Override
	public void increaseStat(Stat stat, int amount) {}

	@Override
	public void onDeath(DamageSource source) {}

	@Override
	public void tick() {}

	@Override
	public void setClientSettings(ClientSettingsC2SPacket packet) {}

	@Override
	public Vec3d getPos() { return Vec3d.ZERO; }

	@Override
	public BlockPos getBlockPos() { return BlockPos.ORIGIN; }

	@Override public void sendSystemMessage(Text component, UUID senderUUID) {}
}

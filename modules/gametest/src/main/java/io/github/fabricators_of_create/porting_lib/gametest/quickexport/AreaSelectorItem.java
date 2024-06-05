package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import net.minecraft.core.component.DataComponents;

import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.world.item.component.CustomData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AreaSelectorItem extends Item {
	public static final String AREA_KEY = "area";

	public static final Component REQUIRES_OP = lang("requires_op");
	public static final Component SUCCESS_FIRST = lang("success_first");
	public static final Component SUCCESS_SECOND = lang("success_second");
	public static final Component TO_RESET = lang("to_reset");
	public static final Component RESET = lang("reset");

	public AreaSelectorItem(Properties settings) {
		super(settings);
	}

	@Override
	@NotNull
	public InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player user, @NotNull InteractionHand hand) {
		ItemStack held = user.getItemInHand(hand);
		if (!(user instanceof ServerPlayer player))
			return InteractionResultHolder.success(held);
		InteractionResult baseResult = baseUse(player, held);
		if (baseResult != null)
			return new InteractionResultHolder<>(baseResult, held);
		selectArea(player, held, getLookTarget(player));
		return InteractionResultHolder.success(held);
	}

	@Override
	@NotNull
	public InteractionResult useOn(@NotNull UseOnContext context) {
		if (!(context.getPlayer() instanceof ServerPlayer player))
			return InteractionResult.SUCCESS;
		ItemStack held = context.getItemInHand();
		InteractionResult baseResult = baseUse(player, held);
		if (baseResult != null)
			return baseResult;

		BlockPos pos = context.getClickedPos();
		selectArea(player, held, pos);

		return InteractionResult.SUCCESS;
	}

	@Nullable
	private InteractionResult baseUse(ServerPlayer player, ItemStack held) {
		if (!player.canUseGameMasterBlocks()) {
			player.sendSystemMessage(REQUIRES_OP, true);
			return InteractionResult.PASS;
		}
		if (player.isShiftKeyDown()) {
			held.remove(DataComponents.CUSTOM_DATA);
			player.sendSystemMessage(RESET, true);
			return InteractionResult.SUCCESS;
		}
		return null;
	}

	private void selectArea(ServerPlayer player, ItemStack held, BlockPos pos) {
		AreaSelection area = getArea(held);
		if (area == null)
			area = new AreaSelection(null, null);

		if (area.first == null) {
			area.first = pos;
			player.sendSystemMessage(SUCCESS_FIRST, true);
			setArea(held, area);
		} else if (area.second == null) {
			area.second = pos;
			player.sendSystemMessage(SUCCESS_SECOND, true);
			setArea(held, area);
		} else {
			player.sendSystemMessage(TO_RESET, true);
		}
	}

	@Override
	public boolean isFoil(@NotNull ItemStack stack) {
		return true;
	}

	public static AreaSelection getArea(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data != null && data.copyTag().contains(AREA_KEY, Tag.TAG_COMPOUND)) {
			AreaSelection area = AreaSelection.fromNbt(data.copyTag().getCompound(AREA_KEY));
			return area.first != null ? area : null; // safety from broken data
		}
		return null;
	}

	public static void setArea(ItemStack stack, AreaSelection area) {
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(area.toNbt()));
	}

	public static BlockPos getLookTarget(Player player) {
		HitResult hit = player.pick(5, 0, false);
		if (hit instanceof BlockHitResult blockHit)
			return blockHit.getBlockPos();
		return BlockPos.containing(hit.getLocation());
	}

	private static Component lang(String type) {
		return Component.translatable("area_selector.use.feedback." + type);
	}
}

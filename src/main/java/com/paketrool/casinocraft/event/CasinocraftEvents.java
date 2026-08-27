package com.paketrool.casinocraft.event;

import com.paketrool.casinocraft.attachment.CasinocraftAttachments;
import com.paketrool.casinocraft.enchantment.CasinocraftEnchantments;
import com.paketrool.casinocraft.enchantment.ChorusPortalData;
import com.paketrool.casinocraft.item.CasinocraftItems;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public final class CasinocraftEvents {
	private CasinocraftEvents() {}

	private static final int TORCH_RADIUS = 3;
	private static final int TORCH_CHECK_INTERVAL = 20;

	public static void init() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.player;
			if (Boolean.TRUE.equals(player.getAttachedOrCreate(CasinocraftAttachments.GUIDE_GIVEN))) return;
			player.setAttached(CasinocraftAttachments.GUIDE_GIVEN, true);
			ItemStack guide = new ItemStack(CasinocraftItems.CASINO_GUIDE);
			if (!player.getInventory().add(guide)) {
				player.drop(guide, false);
			}
		});

		UseItemCallback.EVENT.register(CasinocraftEvents::onUseItem);
		ServerTickEvents.END_SERVER_TICK.register(CasinocraftEvents::onServerTick);
	}

	private static InteractionResult onUseItem(Player player, Level level, InteractionHand hand) {
		if (level.isClientSide()) return InteractionResult.PASS;
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(Items.CHORUS_FRUIT)) return InteractionResult.PASS;
		if (!player.isShiftKeyDown()) return InteractionResult.PASS;
		if (!CasinocraftEnchantments.has(stack, CasinocraftEnchantments.CROUPIERS_CHORUS, level)) return InteractionResult.PASS;

		BlockPos pos = player.blockPosition();
		String dim = level.dimension().location().toString();
		ChorusPortalData.writePos(stack, pos, dim);
		player.displayClientMessage(
			Component.translatable("casinocraft.chorus.saved", pos.getX(), pos.getY(), pos.getZ()),
			true
		);
		level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.7f, 1.4f);
		return InteractionResult.SUCCESS;
	}

	private static void onServerTick(net.minecraft.server.MinecraftServer server) {
		if (server.getTickCount() % TORCH_CHECK_INTERVAL != 0) return;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			igniteAroundIfTorch(player);
		}
	}

	private static boolean isGround(BlockState state) {
		return state.is(BlockTags.DIRT)
			|| state.is(Blocks.DIRT_PATH)
			|| state.is(Blocks.FARMLAND);
	}

	private static void igniteAroundIfTorch(ServerPlayer player) {
		//? if >=1.21.6 {
		/*ServerLevel level = (ServerLevel) player.level();*/
		//?} else
		ServerLevel level = player.serverLevel();
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		boolean hasTorch =
			CasinocraftEnchantments.has(main, CasinocraftEnchantments.CROUPIERS_TORCH, level)
				|| CasinocraftEnchantments.has(off, CasinocraftEnchantments.CROUPIERS_TORCH, level);
		if (!hasTorch) return;

		BlockPos center = player.blockPosition();
		List<BlockPos> candidates = new ArrayList<>();
		for (int dx = -TORCH_RADIUS; dx <= TORCH_RADIUS; dx++) {
			for (int dy = -TORCH_RADIUS; dy <= TORCH_RADIUS; dy++) {
				for (int dz = -TORCH_RADIUS; dz <= TORCH_RADIUS; dz++) {
					BlockPos pos = center.offset(dx, dy, dz);
					if (!level.getBlockState(pos).isAir()) continue;
					if (isGround(level.getBlockState(pos.below()))) continue;
					BlockState fireState = BaseFireBlock.getState(level, pos);
					if (fireState.canSurvive(level, pos)) {
						candidates.add(pos.immutable());
					}
				}
			}
		}
		if (candidates.isEmpty()) return;
		BlockPos pick = candidates.get(level.random.nextInt(candidates.size()));
		level.setBlockAndUpdate(pick, BaseFireBlock.getState(level, pick));
		level.playSound(null, pick, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.4f, 1.2f);
	}
}

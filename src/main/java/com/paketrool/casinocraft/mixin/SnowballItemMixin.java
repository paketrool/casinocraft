package com.paketrool.casinocraft.mixin;

import com.paketrool.casinocraft.enchantment.CasinocraftEnchantments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowballItem.class)
public abstract class SnowballItemMixin {

	@Inject(method = "use", at = @At("RETURN"))
	private void casinocraft$infiniteSnowball(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (level.isClientSide()) return;
		if (player.hasInfiniteMaterials()) return;
		ItemStack stack = player.getItemInHand(hand);
		if (CasinocraftEnchantments.has(stack, CasinocraftEnchantments.CROUPIERS_FLURRY, level)) {
			stack.grow(1);
		}
	}
}

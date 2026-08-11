package com.paketrool.casinocraft.mixin;

import com.paketrool.casinocraft.enchantment.CasinocraftEnchantments;
import com.paketrool.casinocraft.enchantment.ChorusPortalData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportRandomlyConsumeEffect.class)
public abstract class TeleportRandomlyConsumeEffectMixin {

	@Inject(method = "apply", at = @At("TAIL"))
	private void casinocraft$chorusPortal(Level level, ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!(level instanceof ServerLevel)) return;
		if (!CasinocraftEnchantments.has(stack, CasinocraftEnchantments.CROUPIERS_CHORUS, level)) return;

		BlockPos savedPos = ChorusPortalData.readPos(stack);
		if (savedPos == null) return;
		String savedDim = ChorusPortalData.readDim(stack);
		if (!savedDim.equals(level.dimension().location().toString())) return;

		if (entity.isPassenger()) entity.stopRiding();
		if (entity.randomTeleport(savedPos.getX() + 0.5, savedPos.getY(), savedPos.getZ() + 0.5, true)) {
			level.playSound(null, savedPos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
			entity.resetFallDistance();
		}
	}
}

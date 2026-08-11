package com.paketrool.casinocraft.mixin;

import com.paketrool.casinocraft.enchantment.CasinocraftEnchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin {

	@Inject(method = "onHitEntity", at = @At("TAIL"))
	private void casinocraft$freezeOnHit(EntityHitResult result, CallbackInfo ci) {
		Snowball self = (Snowball) (Object) this;
		Level level = self.level();
		if (level.isClientSide) return;

		ItemStack item = self.getItem();
		if (item.isEmpty()) return;
		if (!CasinocraftEnchantments.has(item, CasinocraftEnchantments.CROUPIERS_FLURRY, level)) return;

		if (level.random.nextFloat() >= 0.30f) return;
		Entity entity = result.getEntity();
		if (entity instanceof LivingEntity living) {
			living.setTicksFrozen(living.getTicksFrozen() + 200);
		}
	}
}

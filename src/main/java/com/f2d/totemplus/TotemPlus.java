package com.f2d.totemplus;

import com.f2d.totemplus.totems.BaseTotem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotemPlus implements ModInitializer {
	public static final String MOD_ID = "totemplus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModItems.initialize();

		// Тотемы активируются на смертельном уроне — как ванильный тотем бессмертия,
		// но не спасают от урона, который игнорирует неуязвимость (/kill, пустота).
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (!(entity instanceof ServerPlayer player)) {
				return true;
			}
			if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return true;
			}

			// Главная рука в приоритете, затем вторая: сгорает один тотем за смерть.
			for (InteractionHand hand : InteractionHand.values()) {
				ItemStack stack = player.getItemInHand(hand);
				if (stack.getItem() instanceof BaseTotem totem) {
					totem.activateOnDeath(player, stack);
					return false;
				}
			}
			return true;
		});

		LOGGER.info("Totem+ initialized!");
	}
}

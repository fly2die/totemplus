package com.f2d.totemplus;

import com.f2d.totemplus.totems.BaseTotem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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

		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (!(entity instanceof Player player)) {
				return true;
			}

			// Ищем любой BaseTotem в руках
			for (InteractionHand hand : InteractionHand.values()) {
				ItemStack stack = player.getItemInHand(hand);
				if (stack.getItem() instanceof BaseTotem totem) {
					totem.activate(player, stack, hand);
					return false; // отменяем смерть
				}
			}
			return true;
		});

		LOGGER.info("Totem+ initialized!");
	}
}
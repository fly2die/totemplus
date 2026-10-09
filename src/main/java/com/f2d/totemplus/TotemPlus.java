package com.f2d.totemplus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
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
		LOGGER.info("Totem+ initialized!");
		// Здесь регистрация предметов, событий и т.д.
	}
}
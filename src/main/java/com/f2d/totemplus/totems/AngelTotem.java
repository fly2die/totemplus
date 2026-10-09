package com.f2d.totemplus.totems;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AngelTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/AngelTotem");

    public AngelTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        // TODO: Уникальный эффект Angel Totem
        // Пока ничего — просто лог для проверки

        LOGGER.info("Angel Totem activated for {}!", player.getName().getString());
    }
}
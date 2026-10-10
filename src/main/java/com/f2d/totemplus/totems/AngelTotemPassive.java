package com.f2d.totemplus.totems;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AngelTotemPassive {

    // Множитель скорости. 1.5 = на 50% быстрее. Меняй как хочешь.
    private static final float SPEED_MULTIPLIER = 1.5f;

    public static void onPlayerTick(Player player) {
        // 1. Проверяем, летит ли игрок на элитрах
        if (!player.isFallFlying()) return;

        // 2. Проверяем, есть ли Angel Totem в руках
        if (!hasAngelTotemInHands(player)) return;

        // 3. Ускоряем полёт
        var velocity = player.getDeltaMovement();
        player.setDeltaMovement(
                velocity.x * SPEED_MULTIPLIER,
                velocity.y,
                velocity.z * SPEED_MULTIPLIER
        );
    }

    private static boolean hasAngelTotemInHands(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.is(com.f2d.totemplus.ModItems.ANGEL_TOTEM)
                || offHand.is(com.f2d.totemplus.ModItems.ANGEL_TOTEM);
    }
}
package com.f2d.totemplus.totems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class BaseTotem extends Item {

    public BaseTotem(Properties properties) {
        super(properties);
    }

    /**
     * Уникальный эффект тотема. Переопределяется в наследниках.
     * Вызывается ПОСЛЕ расхода тотема и восстановления здоровья,
     * но ДО анимации и частиц.
     */
    protected abstract void onActivate(Player player, ItemStack stack);

    /**
     * Сколько здоровья восстановить при активации тотема.
     * Возвращает количество HP (2.0 = 1 сердце).
     * Переопределяется в наследниках.
     */
    protected abstract float getHealthOnActivate();

    /**
     * Общая логика активации тотема. Вызывается из TotemPlus.
     */
    public void activate(Player player, ItemStack stack, InteractionHand hand) {
        // 1. Расход тотема
        stack.shrink(1);

        // 2. Восстановление здоровья — своё значение для каждого тотема
        float health = getHealthOnActivate();
        // Ограничиваем максимумом здоровья игрока, чтобы не превысить
        health = Math.min(health, player.getMaxHealth());
        player.setHealth(health);

        // 3. Уникальный эффект конкретного тотема
        onActivate(player, stack);

        // 4. Ванильная анимация тотема
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(
                    new ClientboundEntityEventPacket(player, (byte) 35)
            );
        }

        // 5. Частицы тотема
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    40,
                    0.5, 0.5, 0.5,
                    0.5
            );
        }
    }
}
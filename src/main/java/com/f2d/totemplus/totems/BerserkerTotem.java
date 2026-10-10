package com.f2d.totemplus.totems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BerserkerTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/BerserkerTotem");

    // Сколько здоровья восстанавливается при активации (4.0 = 2 сердца)
    private static final float HEALTH_ON_ACTIVATE = 4.0f;

    public BerserkerTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getHealthOnActivate() {
        return HEALTH_ON_ACTIVATE;
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        LOGGER.info("Berserker Totem onActivate START for {}", player.getName().getString());

        // === Баффы самому игроку ===
        // Сила IV на 15 секунд (300 тиков) — очень мощно
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 3));

        // Скорость III на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 2));

        // Спешка III на 15 секунд (быстрее бьёшь, копаешь)
        player.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, 2));

        // Сопротивление I на 15 секунд (чтобы компенсировать малое HP)
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 0));

        // === Визуальный эффект ===
        if (player.level() instanceof ServerLevel serverLevel) {
            // Красные частицы вокруг игрока — ярость
            serverLevel.sendParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    30,
                    0.5, 0.5, 0.5,
                    0.1
            );

            // Дополнительно — пламя для агрессивного вида
            serverLevel.sendParticles(
                    ParticleTypes.FLAME,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    20,
                    0.4, 0.6, 0.4,
                    0.02
            );
        }

        LOGGER.info("Berserker Totem activated for {}!", player.getName().getString());
    }
}
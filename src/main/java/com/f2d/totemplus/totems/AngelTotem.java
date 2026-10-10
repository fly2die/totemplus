package com.f2d.totemplus.totems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AngelTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/AngelTotem");

    // Радиус действия возмездия (в блоках)
    private static final double RETRIBUTION_RADIUS = 8.0;

    // Урон, наносимый каждому врагу (в полусердцах; 6.0 = 3 сердца)
    private static final float RETRIBUTION_DAMAGE = 6.0f;

    public AngelTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        // === 1. Баффы самому игроку ===
        // Сила II на 15 секунд (300 тиков)
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));

        // Скорость II на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 1));

        // Регенерация I на 20 секунд (400 тиков)
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 0));

        // === 2. Возмездие: урон всем враждебным мобам вокруг ===
        if (player.level() instanceof ServerLevel serverLevel) {
            AABB area = player.getBoundingBox().inflate(RETRIBUTION_RADIUS);

            // Ищем всех враждебных мобов в радиусе
            List<Monster> enemies = serverLevel.getEntitiesOfClass(Monster.class, area);

            for (Monster enemy : enemies) {
                // Наносим магический урон (как от зелья мгновенного урона)
                enemy.hurtServer(serverLevel, player.damageSources().magic(), RETRIBUTION_DAMAGE);

                // Визуальный эффект вокруг каждого врага — искры
                serverLevel.sendParticles(
                        ParticleTypes.CRIT,
                        enemy.getX(),
                        enemy.getY() + enemy.getBbHeight() * 0.5,
                        enemy.getZ(),
                        15,
                        0.3, 0.3, 0.3,
                        0.1
                );
            }

            LOGGER.info("Angel Totem retribution hit {} enemies for {}",
                    enemies.size(), player.getName().getString());
        }

        LOGGER.info("Angel Totem activated for {}!", player.getName().getString());
    }
}
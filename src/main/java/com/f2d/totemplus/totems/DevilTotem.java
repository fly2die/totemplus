package com.f2d.totemplus.totems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DevilTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/DevilTotem");

    // Сколько здоровья восстанавливается при активации (6.0 = 3 сердца)
    private static final float HEALTH_ON_ACTIVATE = 6.0f;

    // Радиус кровавой жатвы (в блоках)
    private static final double HARVEST_RADIUS = 8.0;

    // Урон, наносимый каждому врагу (в полусердцах; 8.0 = 4 сердца)
    private static final float HARVEST_DAMAGE = 8.0f;

    // Сколько здоровья восстанавливается за каждого поражённого врага (2.0 = 1 сердце)
    private static final float HEALTH_PER_KILL = 2.0f;

    // Максимум здоровья, которое можно восстановить жатвой (20.0 = 10 сердец)
    private static final float MAX_HARVEST_HEAL = 20.0f;

    public DevilTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getHealthOnActivate() {
        return HEALTH_ON_ACTIVATE;
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        LOGGER.info("Devil Totem onActivate START for {}", player.getName().getString());

        // === 1. Баффы самому игроку ===
        // Сила II на 15 секунд (300 тиков)
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));

        // Скорость II на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 1));

        // === 2. Кровавая жатва ===
        if (player.level() instanceof ServerLevel serverLevel) {
            var pos = player.position();
            AABB area = new AABB(
                    pos.x - HARVEST_RADIUS, pos.y - HARVEST_RADIUS, pos.z - HARVEST_RADIUS,
                    pos.x + HARVEST_RADIUS, pos.y + HARVEST_RADIUS, pos.z + HARVEST_RADIUS
            );

            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area);
            int count = 0;

            for (LivingEntity entity : entities) {
                if (entity instanceof Player) continue; // пропускаем игроков

                // Наносим магический урон
                entity.hurtServer(serverLevel, player.damageSources().magic(), HARVEST_DAMAGE);

                // Визуальный эффект — красные частицы вокруг врага
                serverLevel.sendParticles(
                        ParticleTypes.DAMAGE_INDICATOR,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() * 0.5,
                        entity.getZ(),
                        15, 0.3, 0.3, 0.3, 0.1
                );

                count++;
            }

            // === 3. Восстановление здоровья за каждого поражённого врага ===
            if (count > 0) {
                float healAmount = Math.min(count * HEALTH_PER_KILL, MAX_HARVEST_HEAL);
                float newHealth = Math.min(player.getHealth() + healAmount, player.getMaxHealth());
                player.setHealth(newHealth);

                LOGGER.info("Devil Totem harvested {} enemies, healed player for {}",
                        count, healAmount);
            }

            LOGGER.info("Devil Totem hit {} entities for {}", count, player.getName().getString());
        }
    }
}
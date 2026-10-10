package com.f2d.totemplus.totems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StormTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/StormTotem");

    // === Здоровье при активации (5 сердец) ===
    private static final float HEALTH_ON_ACTIVATE = 10.0f;

    // === Радиус и цели ===
    private static final double STORM_RADIUS = 15.0;
    private static final int MAX_LIGHTNING_TARGETS = 8;

    // === Урон молнии (6 сердец) ===
    private static final float LIGHTNING_DAMAGE = 12.0f;

    // === Поджог цели (в тиках; 100 = 5 секунд) ===
    private static final int FIRE_TICKS = 100;

    // === Минимальная дистанция от игрока ===
    private static final double MIN_DISTANCE_FROM_PLAYER = 3.0;

    // === Отбрасывание врагов (0.5 = умеренно, 1.0 = сильно) ===
    private static final double KNOCKBACK_STRENGTH = 0.8;

    public StormTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getHealthOnActivate() {
        return HEALTH_ON_ACTIVATE;
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        LOGGER.info("Storm Totem onActivate START for {}", player.getName().getString());

        // === 1. Баффы игроку ===
        // Скорость III на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 2));

        // Регенерация I на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0));

        // Сопротивление урону I на 15 секунд
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 0));

        // Спешка I на 15 секунд (быстрее копаешь и бьёшь)
        player.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, 0));

        // === 2. Призыв молний ===
        if (player.level() instanceof ServerLevel serverLevel) {
            var pos = player.position();
            AABB area = new AABB(
                    pos.x - STORM_RADIUS, pos.y - STORM_RADIUS, pos.z - STORM_RADIUS,
                    pos.x + STORM_RADIUS, pos.y + STORM_RADIUS, pos.z + STORM_RADIUS
            );

            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area);
            int count = 0;

            for (LivingEntity entity : entities) {
                if (entity instanceof Player) continue;
                if (count >= MAX_LIGHTNING_TARGETS) break;

                if (entity.distanceTo(player) < MIN_DISTANCE_FROM_PLAYER) {
                    continue;
                }

                // 1. Урон вручную
                entity.hurtServer(serverLevel, player.damageSources().magic(), LIGHTNING_DAMAGE);

                // 2. Поджог цели
                entity.setRemainingFireTicks(FIRE_TICKS);

                // 3. Отбрасывание от игрока
                Vec3 knockbackDir = entity.position().subtract(player.position()).normalize();
                entity.push(
                        knockbackDir.x * KNOCKBACK_STRENGTH,
                        0.3,
                        knockbackDir.z * KNOCKBACK_STRENGTH
                );

                // 4. Визуальная молния
                LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, serverLevel);
                lightning.setVisualOnly(true);
                lightning.setPos(Vec3.atBottomCenterOf(entity.blockPosition()));
                serverLevel.addFreshEntity(lightning);

                count++;
            }

            LOGGER.info("Storm Totem struck {} enemies for {}",
                    count, player.getName().getString());
        }
    }
}
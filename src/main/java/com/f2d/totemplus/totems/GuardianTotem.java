package com.f2d.totemplus.totems;

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

public class GuardianTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/GuardianTotem");

    // Сколько здоровья восстанавливается при активации (20.0 = 10 сердец)
    private static final float HEALTH_ON_ACTIVATE = 12.0f;

    // Радиус действия слабости на врагов (в блоках)
    private static final double WEAKNESS_RADIUS = 10.0;

    public GuardianTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getHealthOnActivate() {
        return HEALTH_ON_ACTIVATE;
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        LOGGER.info("Guardian Totem onActivate START for {}", player.getName().getString());

        // === 1. Мощные защитные баффы ===
        // Сопротивление III на 20 секунд (400 тиков) — сильно снижает урон
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 2));

        // Поглощение IV на 30 секунд (600 тиков) — золотые сердца сверху
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 3));

        // Регенерация II на 20 секунд (400 тиков)
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1));

        // === 2. Снятие всех негативных эффектов ===
        player.removeEffect(MobEffects.POISON);
        player.removeEffect(MobEffects.WITHER);
        player.removeEffect(MobEffects.WEAKNESS);
        player.removeEffect(MobEffects.SLOWNESS);
        player.removeEffect(MobEffects.HUNGER);
        player.removeEffect(MobEffects.NAUSEA);
        player.removeEffect(MobEffects.BLINDNESS);
        player.removeEffect(MobEffects.DARKNESS);
        player.removeEffect(MobEffects.UNLUCK);
        player.removeEffect(MobEffects.MINING_FATIGUE);
        player.removeEffect(MobEffects.LEVITATION);

        // === 3. Слабость всем врагам вокруг ===
        if (player.level() instanceof ServerLevel serverLevel) {
            var pos = player.position();
            AABB area = new AABB(
                    pos.x - WEAKNESS_RADIUS, pos.y - WEAKNESS_RADIUS, pos.z - WEAKNESS_RADIUS,
                    pos.x + WEAKNESS_RADIUS, pos.y + WEAKNESS_RADIUS, pos.z + WEAKNESS_RADIUS
            );

            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area);
            int count = 0;

            for (LivingEntity entity : entities) {
                if (entity instanceof Player) continue;

                // Слабость II на 10 секунд (200 тиков)
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));

                count++;
            }

            LOGGER.info("Guardian Totem weakened {} enemies for {}",
                    count, player.getName().getString());
        }

        LOGGER.info("Guardian Totem activated for {}!", player.getName().getString());
    }
}
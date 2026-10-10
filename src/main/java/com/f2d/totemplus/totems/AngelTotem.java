package com.f2d.totemplus.totems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AngelTotem extends BaseTotem {

    private static final Logger LOGGER = LoggerFactory.getLogger("totemplus/AngelTotem");
    private static final float HEALTH_ON_ACTIVATE = 10.0f;
    private static final double LEVITATION_RADIUS = 8.0;
    private static final int LEVITATION_DURATION = 100;
    private static final int LEVITATION_AMPLIFIER = 1;

    public AngelTotem(Properties properties) {
        super(properties);
    }

    @Override
    protected float getHealthOnActivate() {
        return HEALTH_ON_ACTIVATE;
    }

    @Override
    protected void onActivate(Player player, ItemStack stack) {
        LOGGER.info("onActivate START for {}", player.getName().getString());

        // Баффы игроку
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 0));

        if (player.level() instanceof ServerLevel serverLevel) {
            // Кубическая зона вокруг игрока по координатам
            var pos = player.position();
            AABB area = new AABB(
                    pos.x - LEVITATION_RADIUS, pos.y - LEVITATION_RADIUS, pos.z - LEVITATION_RADIUS,
                    pos.x + LEVITATION_RADIUS, pos.y + LEVITATION_RADIUS, pos.z + LEVITATION_RADIUS
            );

            // Ищем ВСЕХ живых сущностей, кроме игрока
            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(LivingEntity.class, area);
            int count = 0;

            for (LivingEntity entity : entities) {
                if (entity instanceof Player) continue; // пропускаем игроков

                entity.addEffect(new MobEffectInstance(
                        MobEffects.LEVITATION,
                        LEVITATION_DURATION,
                        LEVITATION_AMPLIFIER
                ));

                serverLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() * 0.5,
                        entity.getZ(),
                        20, 0.3, 0.5, 0.3, 0.05
                );

                count++;
            }

            LOGGER.info("Levitated {} entities for {}", count, player.getName().getString());
        }
    }
}
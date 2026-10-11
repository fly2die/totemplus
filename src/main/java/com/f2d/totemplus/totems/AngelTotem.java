package com.f2d.totemplus.totems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/** Ангельский тотем: лечит больше всех, усиливает и поднимает врагов в воздух. */
public class AngelTotem extends BaseTotem {

    /** 10 HP = 5 сердец. */
    private static final float HEALTH_ON_ACTIVATE = 10.0F;

    private static final int BUFF_DURATION = 300;
    private static final int REGENERATION_DURATION = 400;

    private static final double LEVITATION_RADIUS = 8.0;
    private static final int LEVITATION_DURATION = 100;
    private static final int LEVITATION_AMPLIFIER = 1;

    public AngelTotem(Properties properties) {
        super(properties, HEALTH_ON_ACTIVATE);
    }

    @Override
    protected void onActivate(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, BUFF_DURATION, 1));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, BUFF_DURATION, 1));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION, 0));

        int levitated = 0;
        for (LivingEntity enemy : enemiesAround(player, level, LEVITATION_RADIUS)) {
            enemy.addEffect(new MobEffectInstance(MobEffects.LEVITATION, LEVITATION_DURATION, LEVITATION_AMPLIFIER));

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    enemy.getX(),
                    enemy.getY() + enemy.getBbHeight() * 0.5,
                    enemy.getZ(),
                    20, 0.3, 0.5, 0.3, 0.05
            );
            levitated++;
        }

        LOGGER.debug("Angel Totem: levitated {} enemies for {}",
                levitated, player.getName().getString());
    }

    @Override
    protected List<Component> effectTooltip() {
        return List.of(
                Component.translatable("tooltip.totemplus.angel_totem.buffs")
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.totemplus.angel_totem.levitation", (int) LEVITATION_RADIUS)
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}

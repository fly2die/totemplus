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

/** Дьявольский тотем: кровавая жатва — урон всем врагам вокруг и лечение за каждого задетого. */
public class DevilTotem extends BaseTotem {

    /** 6 HP = 3 сердца. */
    private static final float HEALTH_ON_ACTIVATE = 6.0F;

    private static final int BUFF_DURATION = 300;

    private static final double HARVEST_RADIUS = 8.0;
    /** 8 HP = 4 сердца урона каждому врагу. */
    private static final float HARVEST_DAMAGE = 8.0F;
    /** 2 HP = 1 сердце за каждого задетого врага. */
    private static final float HEALTH_PER_VICTIM = 2.0F;
    /** Потолок лечения с одной активации: 20 HP = 10 сердец. */
    private static final float MAX_HARVEST_HEAL = 20.0F;

    public DevilTotem(Properties properties) {
        super(properties, HEALTH_ON_ACTIVATE);
    }

    @Override
    protected void onActivate(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, BUFF_DURATION, 1));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, BUFF_DURATION, 1));

        int victims = 0;
        for (LivingEntity enemy : enemiesAround(player, level, HARVEST_RADIUS)) {
            enemy.hurtServer(level, player.damageSources().magic(), HARVEST_DAMAGE);

            level.sendParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    enemy.getX(),
                    enemy.getY() + enemy.getBbHeight() * 0.5,
                    enemy.getZ(),
                    15, 0.3, 0.3, 0.3, 0.1
            );
            victims++;
        }

        // heal() сам ограничивает здоровье максимумом игрока.
        float heal = Math.min(victims * HEALTH_PER_VICTIM, MAX_HARVEST_HEAL);
        if (heal > 0.0F) {
            player.heal(heal);
        }

        LOGGER.debug("Devil Totem: harvested {} enemies, healed {} HP for {}",
                victims, heal, player.getName().getString());
    }

    @Override
    protected List<Component> effectTooltip() {
        return List.of(
                Component.translatable("tooltip.totemplus.devil_totem.buffs")
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.totemplus.devil_totem.harvest",
                                (int) (HARVEST_DAMAGE / 2.0F), (int) HARVEST_RADIUS)
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}

package com.f2d.totemplus.totems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Штормовой тотем: набор защитных баффов и молнии по врагам вокруг. */
public class StormTotem extends BaseTotem {

    /** 10 HP = 5 сердец. */
    private static final float HEALTH_ON_ACTIVATE = 10.0F;

    private static final int BUFF_DURATION = 300;

    private static final double STORM_RADIUS = 15.0;
    private static final int MAX_LIGHTNING_TARGETS = 8;
    /** 12 HP = 6 сердец урона. */
    private static final float LIGHTNING_DAMAGE = 12.0F;
    /** 100 тиков = 5 секунд горения. */
    private static final int FIRE_TICKS = 100;
    /** Ближе этого расстояния молния не бьёт, чтобы не задеть игрока. */
    private static final double MIN_DISTANCE_FROM_PLAYER = 3.0;
    private static final double KNOCKBACK_STRENGTH = 0.8;

    public StormTotem(Properties properties) {
        super(properties, HEALTH_ON_ACTIVATE);
    }

    @Override
    protected void onActivate(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, BUFF_DURATION, 2));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BUFF_DURATION, 0));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, BUFF_DURATION, 0));
        player.addEffect(new MobEffectInstance(MobEffects.HASTE, BUFF_DURATION, 0));

        int struck = 0;
        for (LivingEntity enemy : enemiesAround(player, level, STORM_RADIUS)) {
            if (struck >= MAX_LIGHTNING_TARGETS) {
                break;
            }
            if (enemy.distanceTo(player) < MIN_DISTANCE_FROM_PLAYER) {
                continue;
            }

            // Урон считаем вручную, поэтому сама молния — только визуальная.
            enemy.hurtServer(level, player.damageSources().magic(), LIGHTNING_DAMAGE);
            enemy.setRemainingFireTicks(FIRE_TICKS);

            // distanceTo выше гарантирует ненулевую длину вектора, деления на ноль нет.
            Vec3 knockback = enemy.position().subtract(player.position()).normalize();
            enemy.push(knockback.x * KNOCKBACK_STRENGTH, 0.3, knockback.z * KNOCKBACK_STRENGTH);

            LightningBolt lightning = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
            lightning.setVisualOnly(true);
            lightning.setPos(Vec3.atBottomCenterOf(enemy.blockPosition()));
            level.addFreshEntity(lightning);

            struck++;
        }

        LOGGER.debug("Storm Totem: struck {} enemies for {}",
                struck, player.getName().getString());
    }

    @Override
    protected List<Component> effectTooltip() {
        return List.of(
                Component.translatable("tooltip.totemplus.storm_totem.buffs")
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.totemplus.storm_totem.lightning",
                                MAX_LIGHTNING_TARGETS, (int) STORM_RADIUS)
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}

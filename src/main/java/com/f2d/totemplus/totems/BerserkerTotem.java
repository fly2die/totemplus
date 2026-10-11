package com.f2d.totemplus.totems;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

/** Тотем берсерка: лечит меньше всех, зато даёт самую сильную атаку. */
public class BerserkerTotem extends BaseTotem {

    /** 4 HP = 2 сердца. */
    private static final float HEALTH_ON_ACTIVATE = 4.0F;

    private static final int BUFF_DURATION = 300;

    public BerserkerTotem(Properties properties) {
        super(properties, HEALTH_ON_ACTIVATE);
    }

    @Override
    protected void onActivate(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, BUFF_DURATION, 3));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, BUFF_DURATION, 2));
        player.addEffect(new MobEffectInstance(MobEffects.HASTE, BUFF_DURATION, 2));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, BUFF_DURATION, 0));

        // Красные частицы — ярость, пламя — агрессивный вид.
        level.sendParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1
        );
        level.sendParticles(
                ParticleTypes.FLAME,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                20, 0.4, 0.6, 0.4, 0.02
        );

        LOGGER.debug("Berserker Totem: activated for {}", player.getName().getString());
    }

    @Override
    protected List<Component> effectTooltip() {
        return List.of(
                Component.translatable("tooltip.totemplus.berserker_totem.buffs")
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}

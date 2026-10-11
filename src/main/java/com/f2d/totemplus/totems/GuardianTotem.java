package com.f2d.totemplus.totems;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/** Тотем стража: сильная защита, снятие негативных эффектов и слабость на врагов вокруг. */
public class GuardianTotem extends BaseTotem {

    /** 12 HP = 6 сердец. */
    private static final float HEALTH_ON_ACTIVATE = 12.0F;

    private static final int BUFF_DURATION = 400;
    private static final int ABSORPTION_DURATION = 600;

    private static final double WEAKNESS_RADIUS = 10.0;
    private static final int WEAKNESS_DURATION = 200;

    public GuardianTotem(Properties properties) {
        super(properties, HEALTH_ON_ACTIVATE);
    }

    @Override
    protected void onActivate(ServerPlayer player, ServerLevel level) {
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, BUFF_DURATION, 2));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ABSORPTION_DURATION, 3));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BUFF_DURATION, 1));

        clearHarmfulEffects(player);

        int weakened = 0;
        for (LivingEntity enemy : enemiesAround(player, level, WEAKNESS_RADIUS)) {
            enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 1));
            weakened++;
        }

        LOGGER.debug("Guardian Totem: weakened {} enemies for {}",
                weakened, player.getName().getString());
    }

    /**
     * Снимает все вредные эффекты за один проход. Ручной список пришлось бы дополнять
     * при каждом обновлении игры: новые вредные эффекты в нём сами не появятся.
     */
    private static void clearHarmfulEffects(ServerPlayer player) {
        player.getActiveEffects().stream()
                .map(MobEffectInstance::getEffect)
                .filter(effect -> effect.value().getCategory() == MobEffectCategory.HARMFUL)
                .toList()
                .forEach(effect -> player.removeEffect(effect));
    }

    @Override
    protected List<Component> effectTooltip() {
        return List.of(
                Component.translatable("tooltip.totemplus.guardian_totem.buffs")
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.totemplus.guardian_totem.cleanse")
                        .withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.totemplus.guardian_totem.weakness", (int) WEAKNESS_RADIUS)
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}

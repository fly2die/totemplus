package com.f2d.totemplus.totems;

import com.f2d.totemplus.TotemPlus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Consumer;

/**
 * Общая часть всех тотемов: активация на смертельном уроне, восстановление здоровья,
 * ванильная анимация и частицы. Наследник описывает только свой уникальный эффект.
 */
public abstract class BaseTotem extends Item {

    protected static final Logger LOGGER = TotemPlus.LOGGER;

    /**
     * "Второй шанс" — регенерация II и поглощение II на 5 секунд, огнестойкость на 10 секунд.
     * Без этого игрок с 2–6 сердцами умирает от следующего же удара, поэтому ванильный
     * тотем бессмертия даёт ровно такой же набор.
     */
    private static final int SECOND_CHANCE_DURATION = 100;
    private static final int FIRE_RESISTANCE_DURATION = 200;

    /** Ванильная анимация тотема (статус 35 в протоколе). */
    private static final byte TOTEM_ANIMATION_STATUS = 35;

    private final float healthOnActivate;

    protected BaseTotem(Properties properties, float healthOnActivate) {
        super(properties);
        this.healthOnActivate = healthOnActivate;
    }

    /**
     * Уникальный эффект тотема. Вызывается после расхода тотема и восстановления здоровья,
     * но до анимации и частиц. Уровень всегда серверный.
     */
    protected abstract void onActivate(ServerPlayer player, ServerLevel level);

    /** Строки с описанием эффекта для подсказки предмета, без общих строк из BaseTotem. */
    protected abstract List<Component> effectTooltip();

    /** Вызывается из {@link TotemPlus}, когда игрок должен был умереть. */
    public final void activateOnDeath(ServerPlayer player, ItemStack stack) {
        // 1. Расход тотема.
        stack.shrink(1);

        // 2. Здоровье — своё для каждого тотема, но не выше максимума игрока.
        player.setHealth(Math.min(this.healthOnActivate, player.getMaxHealth()));

        // 3. Второй шанс.
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, SECOND_CHANCE_DURATION, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, SECOND_CHANCE_DURATION, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_DURATION, 0));

        // 4. Уникальный эффект тотема и частицы активации.
        if (player.level() instanceof ServerLevel level) {
            onActivate(player, level);

            level.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    40,
                    0.5, 0.5, 0.5,
                    0.5
            );
        }

        // 5. Ванильная анимация тотема.
        player.connection.send(new ClientboundEntityEventPacket(player, TOTEM_ANIMATION_STATUS));
    }

    /**
     * Враждебные мобы вокруг игрока. {@link Enemy} отсекает чужих питомцев, жителей,
     * големов и прочих мирных сущностей, которые раньше попадали под эффект тотемов.
     * Предварительный отбор идёт по кубу вокруг хитбокса, а точная проверка — по расстоянию,
     * чтобы углы куба не попадали в зону действия.
     */
    protected static List<LivingEntity> enemiesAround(ServerPlayer player, ServerLevel level, double radius) {
        double radiusSquared = radius * radius;
        return level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius)).stream()
                .filter(entity -> entity instanceof Enemy)
                .filter(entity -> entity.distanceToSqr(player) <= radiusSquared)
                .toList();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> adder, TooltipFlag flag) {
        adder.accept(Component.translatable("tooltip.totemplus.activation").withStyle(ChatFormatting.DARK_GRAY));
        adder.accept(Component.translatable("tooltip.totemplus.heal", (int) (this.healthOnActivate / 2.0F))
                .withStyle(ChatFormatting.GRAY));
        this.effectTooltip().forEach(adder);
    }
}

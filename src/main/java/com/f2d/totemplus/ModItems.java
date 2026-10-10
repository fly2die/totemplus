package com.f2d.totemplus;

import com.f2d.totemplus.totems.AngelTotem;
import com.f2d.totemplus.totems.BerserkerTotem;
import com.f2d.totemplus.totems.DevilTotem;
import com.f2d.totemplus.totems.GuardianTotem;
import com.f2d.totemplus.totems.StormTotem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ModItems {

    // === Angel Totem ===
    public static final ResourceKey<Item> ANGEL_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "angel_totem"));

    public static final Item ANGEL_TOTEM = register(
            ANGEL_TOTEM_KEY,
            AngelTotem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    // === Devil Totem ===
    public static final ResourceKey<Item> DEVIL_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "devil_totem"));

    public static final Item DEVIL_TOTEM = register(
            DEVIL_TOTEM_KEY,
            DevilTotem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    // === Storm Totem ===
    public static final ResourceKey<Item> STORM_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "storm_totem"));

    public static final Item STORM_TOTEM = register(
            STORM_TOTEM_KEY,
            StormTotem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    // === Berserker Totem ===
    public static final ResourceKey<Item> BERSERKER_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "berserker_totem"));

    public static final Item BERSERKER_TOTEM = register(
            BERSERKER_TOTEM_KEY,
            BerserkerTotem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    // === Guardian Totem ===
    public static final ResourceKey<Item> GUARDIAN_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "guardian_totem"));

    public static final Item GUARDIAN_TOTEM = register(
            GUARDIAN_TOTEM_KEY,
            GuardianTotem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static Item register(ResourceKey<Item> key,
                                Function<Item.Properties, Item> factory,
                                Item.Properties settings) {
        Item item = factory.apply(settings.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(output -> {
                    output.insertAfter(Items.TOTEM_OF_UNDYING, ANGEL_TOTEM);
                    output.insertAfter(ANGEL_TOTEM, DEVIL_TOTEM);
                    output.insertAfter(DEVIL_TOTEM, STORM_TOTEM);
                    output.insertAfter(STORM_TOTEM, BERSERKER_TOTEM);
                    output.insertAfter(BERSERKER_TOTEM, GUARDIAN_TOTEM);
                });
    }
}
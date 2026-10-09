package com.f2d.totemplus;

import com.f2d.totemplus.totems.AngelTotem;
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

    public static final ResourceKey<Item> ANGEL_TOTEM_KEY =
            ResourceKey.create(Registries.ITEM,
                    Identifier.fromNamespaceAndPath(TotemPlus.MOD_ID, "angel_totem"));

    public static final Item ANGEL_TOTEM = register(
            ANGEL_TOTEM_KEY,
            AngelTotem::new,   // ← теперь используем наш класс
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
                .register(output -> output.insertAfter(Items.TOTEM_OF_UNDYING, ANGEL_TOTEM));
    }
}
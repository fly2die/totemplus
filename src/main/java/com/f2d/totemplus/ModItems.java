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
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

public class ModItems {

    public static final Item ANGEL_TOTEM = register("angel_totem", AngelTotem::new);
    public static final Item DEVIL_TOTEM = register("devil_totem", DevilTotem::new);
    public static final Item STORM_TOTEM = register("storm_totem", StormTotem::new);
    public static final Item BERSERKER_TOTEM = register("berserker_totem", BerserkerTotem::new);
    public static final Item GUARDIAN_TOTEM = register("guardian_totem", GuardianTotem::new);

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, TotemPlus.id(name));
        Item.Properties properties = new Item.Properties().stacksTo(1).rarity(Rarity.RARE).setId(key);
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties));
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.insertAfter(Items.TOTEM_OF_UNDYING, ANGEL_TOTEM);
            output.insertAfter(ANGEL_TOTEM, DEVIL_TOTEM);
            output.insertAfter(DEVIL_TOTEM, STORM_TOTEM);
            output.insertAfter(STORM_TOTEM, BERSERKER_TOTEM);
            output.insertAfter(BERSERKER_TOTEM, GUARDIAN_TOTEM);
        });
    }
}

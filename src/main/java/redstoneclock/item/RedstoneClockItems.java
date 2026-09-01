package redstoneclock.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import redstoneclock.RedstoneClock;
import redstoneclock.block.RedstoneClockBlocks;

public class RedstoneClockItems {
    public static ClockBlockItem CLOCK;

    interface ItemMaker<T extends Item> {
        T makeItem(ResourceKey<Item> key);
    }

    private static <T extends Item> T registerItem(String name, ItemMaker<T> itemMaker) {
        RedstoneClock.LOGGER.info("Registering item: " + RedstoneClock.MODID + ":" + name);

        var identifier = Identifier.fromNamespaceAndPath(RedstoneClock.MODID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, identifier);
        T item = itemMaker.makeItem(key);
        return Registry.register(BuiltInRegistries.ITEM, identifier, item);
    }

    public static void registerItems() {
        RedstoneClock.LOGGER.info("Registering mod items for " + RedstoneClock.MODID);

        CLOCK = registerItem("clock", key -> { 
            return new ClockBlockItem(RedstoneClockBlocks.CLOCK, new Item.Properties().useBlockDescriptionPrefix().setId(key)); 
        });
    }    
}

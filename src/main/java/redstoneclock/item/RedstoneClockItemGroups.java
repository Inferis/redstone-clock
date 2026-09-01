package redstoneclock.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import redstoneclock.RedstoneClock;

public class RedstoneClockItemGroups {
    public static void registerItemGroups() {
        RedstoneClock.LOGGER.info("Registering item groups for " + RedstoneClock.MODID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(content -> {
            content.accept(RedstoneClockItems.CLOCK);
        });
    }    
}

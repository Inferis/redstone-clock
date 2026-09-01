package redstoneclock.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import redstoneclock.RedstoneClock;

public class RedstoneClockBlocks {
    public static ClockBlock CLOCK;

    interface BlockMaker<T extends Block> {
        T makeBlock(ResourceKey<Block> key);
    }

    private static <T extends Block> T registerBlock(String name, BlockMaker<T> blockMaker) {
        RedstoneClock.LOGGER.info("Registering block: " + RedstoneClock.MODID + ":" + name);

        var identifier = Identifier.fromNamespaceAndPath(RedstoneClock.MODID, name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, identifier);
        return Registry.register(
            BuiltInRegistries.BLOCK,
            identifier,
            blockMaker.makeBlock(key));
    }


    public static void registerBlocks() {
        CLOCK = registerBlock("clock", key -> { 
            return new ClockBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER).setId(key)); 
        });
    }
    
}

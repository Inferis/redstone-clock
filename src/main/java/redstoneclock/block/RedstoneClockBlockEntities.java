package redstoneclock.block;

import redstoneclock.RedstoneClock;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class RedstoneClockBlockEntities {
    public static BlockEntityType<ClockBlockEntity> CLOCK_ENTITY;
    
    public static void registerBlockEntities() {
        CLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(RedstoneClock.MODID, "clock_block_entity"),
            FabricBlockEntityTypeBuilder.create(ClockBlockEntity::new, RedstoneClockBlocks.CLOCK).build()
        );    
    }
}

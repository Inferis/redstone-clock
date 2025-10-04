package redstoneclock.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import redstoneclock.networking.OpenEditorS2CPayload;
import redstoneclock.networking.SaveIntervalsC2SPayload;

public class ClockBlockEntity extends BlockEntity {
    private final String ACTIVE_INTERVAL_KEY = "active_interval";
    private final String INACTIVE_INTERVAL_KEY = "inactive_interval";
    private final String SIGNAL_STRENGTH_KEY = "signal_strength";

    private int signalStrength = 15;
    private int cycle = 0;
    private int activeInterval = 20;
    private int inactiveInterval = 20;

    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(RedstoneClockBlockEntities.CLOCK_ENTITY, pos, state);
    }

    public int getSignalStrength() {
        return cycle < activeInterval ? signalStrength : 0;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (state.get(Properties.POWERED)) {
            cycle = (cycle + 1) % (activeInterval + inactiveInterval);
            var isActive = state.get(RedstoneClockProperties.ACTIVE);
            if (cycle < activeInterval) {
                if (!isActive) {
                    world.setBlockState(pos, state.with(RedstoneClockProperties.ACTIVE, true));
                }
            }
            else if (isActive) {
                world.setBlockState(pos, state.with(RedstoneClockProperties.ACTIVE, false));
            }
            world.scheduleBlockTick(pos, state.getBlock(), 1);
        }
        else {
            cycle = -1;
            world.setBlockState(pos, state.with(RedstoneClockProperties.ACTIVE, false));
        }
    }

    public OpenEditorS2CPayload getEditorPayload(BlockPos pos) {
        return new OpenEditorS2CPayload(pos, activeInterval, inactiveInterval, signalStrength);
    }

    public void updateFromPayload(SaveIntervalsC2SPayload payload) {
        this.activeInterval = payload.activeInterval();
        this.inactiveInterval = payload.inactiveInterval();
        this.signalStrength = payload.signalStrength();
        this.markDirty();
    }

    @Override
    public void readData(ReadView view) {
        super.readData(view);

        if (view.contains(ACTIVE_INTERVAL_KEY)) {
            activeInterval = view.getInt(ACTIVE_INTERVAL_KEY, 20);
        }
        if (view.contains(INACTIVE_INTERVAL_KEY)) {
            inactiveInterval = view.getInt(INACTIVE_INTERVAL_KEY, 20);
        }
        if (view.contains(SIGNAL_STRENGTH_KEY)) {
            signalStrength = view.getInt(SIGNAL_STRENGTH_KEY, 15);
        }
    }
    
    @Override
    public void writeData(WriteView view) {
        super.writeData(view);

        view.putInt(ACTIVE_INTERVAL_KEY, activeInterval);
        view.putInt(INACTIVE_INTERVAL_KEY, inactiveInterval);
        view.putInt(SIGNAL_STRENGTH_KEY, signalStrength);
    }
}

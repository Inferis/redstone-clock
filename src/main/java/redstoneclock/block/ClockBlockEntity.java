package redstoneclock.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            cycle = (cycle + 1) % (activeInterval + inactiveInterval);
            var isActive = state.getValue(RedstoneClockProperties.ACTIVE);
            if (cycle < activeInterval) {
                if (!isActive) {
                    world.setBlockAndUpdate(pos, state.setValue(RedstoneClockProperties.ACTIVE, true));
                }
            }
            else if (isActive) {
                world.setBlockAndUpdate(pos, state.setValue(RedstoneClockProperties.ACTIVE, false));
            }
            world.scheduleTick(pos, state.getBlock(), 1);
        }
        else {
            cycle = -1;
            world.setBlockAndUpdate(pos, state.setValue(RedstoneClockProperties.ACTIVE, false));
        }
    }

    public OpenEditorS2CPayload getEditorPayload(BlockPos pos) {
        return new OpenEditorS2CPayload(pos, activeInterval, inactiveInterval, signalStrength);
    }

    public void updateFromPayload(SaveIntervalsC2SPayload payload) {
        this.activeInterval = payload.activeInterval();
        this.inactiveInterval = payload.inactiveInterval();
        this.signalStrength = payload.signalStrength();
        this.setChanged();
    }

    @Override
    public void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        if (view.contains(ACTIVE_INTERVAL_KEY)) {
            activeInterval = view.getIntOr(ACTIVE_INTERVAL_KEY, 20);
        }
        if (view.contains(INACTIVE_INTERVAL_KEY)) {
            inactiveInterval = view.getIntOr(INACTIVE_INTERVAL_KEY, 20);
        }
        if (view.contains(SIGNAL_STRENGTH_KEY)) {
            signalStrength = view.getIntOr(SIGNAL_STRENGTH_KEY, 15);
        }
    }
    
    @Override
    public void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt(ACTIVE_INTERVAL_KEY, activeInterval);
        view.putInt(INACTIVE_INTERVAL_KEY, inactiveInterval);
        view.putInt(SIGNAL_STRENGTH_KEY, signalStrength);
    }
}

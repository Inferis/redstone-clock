package redstoneclock.block;

import com.mojang.serialization.MapCodec;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ClockBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public ClockBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(BlockStateProperties.POWERED, false)
            .setValue(RedstoneClockProperties.ACTIVE, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClockBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.POWERED);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(RedstoneClockProperties.ACTIVE);
    }

    @Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
	}

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock,
            Orientation wireOrientation, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);
        scheduleRedstoneTick(world, pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        scheduleRedstoneTick(world, pos, state);
    }

    private static void scheduleRedstoneTick(Level world, BlockPos pos, BlockState state) {
        var power = 0;
        var direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        var dirPos = pos.relative(direction);
        power = world.getSignal(dirPos, direction);
        world.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, power > 0));
        world.scheduleTick(pos, state.getBlock(), 1);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return state.getSignal(world, pos, direction);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        if (direction.getAxis().isVertical()) {
            return 0;
        }

        if (state.getValue(RedstoneClockProperties.ACTIVE) && world.getBlockEntity(pos) instanceof ClockBlockEntity clockEntity) {
            var oppositeFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            if (direction != Direction.UP && direction != Direction.DOWN && direction != oppositeFacing) {
                return clockEntity.getSignalStrength();
            }
        }

        return 0;
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random); 
        if (world.getBlockEntity(pos) instanceof ClockBlockEntity clockEntity) {
            clockEntity.tick(world, pos, state);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof ClockBlockEntity clockBlockEntity) {
            ServerPlayNetworking.send((ServerPlayer)player, clockBlockEntity.getEditorPayload(pos));
        }
        return InteractionResult.SUCCESS;
    }
}


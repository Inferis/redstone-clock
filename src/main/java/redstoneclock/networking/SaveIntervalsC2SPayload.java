package redstoneclock.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import redstoneclock.RedstoneClock;

public record SaveIntervalsC2SPayload(BlockPos blockPos, int activeInterval, int inactiveInterval, int signalStrength) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SaveIntervalsC2SPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RedstoneClock.MODID, "save_intervals_c2s_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveIntervalsC2SPayload> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, SaveIntervalsC2SPayload::blockPos,
        ByteBufCodecs.INT, SaveIntervalsC2SPayload::activeInterval,
        ByteBufCodecs.INT, SaveIntervalsC2SPayload::inactiveInterval, 
        ByteBufCodecs.INT, SaveIntervalsC2SPayload::signalStrength, 
        SaveIntervalsC2SPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

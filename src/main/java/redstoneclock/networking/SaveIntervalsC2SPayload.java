package redstoneclock.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import redstoneclock.RedstoneClock;

public record SaveIntervalsC2SPayload(BlockPos blockPos, int activeInterval, int inactiveInterval, int signalStrength) implements CustomPayload {
    public static final CustomPayload.Id<SaveIntervalsC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(RedstoneClock.MODID, "save_intervals_c2s_payload"));

    public static final PacketCodec<RegistryByteBuf, SaveIntervalsC2SPayload> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC, SaveIntervalsC2SPayload::blockPos,
        PacketCodecs.INTEGER, SaveIntervalsC2SPayload::activeInterval,
        PacketCodecs.INTEGER, SaveIntervalsC2SPayload::inactiveInterval, 
        PacketCodecs.INTEGER, SaveIntervalsC2SPayload::signalStrength, 
        SaveIntervalsC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

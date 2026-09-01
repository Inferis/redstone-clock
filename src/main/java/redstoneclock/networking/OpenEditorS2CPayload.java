package redstoneclock.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import redstoneclock.RedstoneClock;

public record OpenEditorS2CPayload(BlockPos blockPos, int activeInterval, int inactiveInterval, int signalStrength) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenEditorS2CPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(RedstoneClock.MODID, "open_editor_s2c_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEditorS2CPayload> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, OpenEditorS2CPayload::blockPos, 
        ByteBufCodecs.INT, OpenEditorS2CPayload::activeInterval,
        ByteBufCodecs.INT, OpenEditorS2CPayload::inactiveInterval, 
        ByteBufCodecs.INT, OpenEditorS2CPayload::signalStrength, 
        OpenEditorS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

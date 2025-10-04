package redstoneclock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import redstoneclock.block.ClockBlockEntity;
import redstoneclock.block.RedstoneClockBlockEntities;
import redstoneclock.block.RedstoneClockBlocks;
import redstoneclock.item.RedstoneClockItemGroups;
import redstoneclock.item.RedstoneClockItems;
import redstoneclock.networking.OpenEditorS2CPayload;
import redstoneclock.networking.SaveIntervalsC2SPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedstoneClock implements ModInitializer {
	public static final String MODID = "redstoneclock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final RedstoneClockConfig CONFIG = new RedstoneClockConfig();

	@Override
	public void onInitialize() {
		CONFIG.initialLoad();

		RedstoneClockBlocks.registerBlocks();
		RedstoneClockBlockEntities.registerBlockEntities();
		RedstoneClockItems.registerItems();
		RedstoneClockItemGroups.registerItemGroups();

		registerNetworkPayloads();
		registerNetworkReceivers();
	}

	private void registerNetworkPayloads() {
		PayloadTypeRegistry.playS2C().register(OpenEditorS2CPayload.ID, OpenEditorS2CPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SaveIntervalsC2SPayload.ID, SaveIntervalsC2SPayload.CODEC);
	}

	private void registerNetworkReceivers() {
		ServerPlayNetworking.registerGlobalReceiver(SaveIntervalsC2SPayload.ID, RedstoneClock::receiveSaveIntervalsPayload);
	}

	public static void receiveSaveIntervalsPayload(SaveIntervalsC2SPayload payload, ServerPlayNetworking.Context context) {
        if (context.player().getEntityWorld().getBlockEntity(payload.blockPos()) instanceof ClockBlockEntity clockBlockEntity) {
			clockBlockEntity.updateFromPayload(payload);
		}
	}
}
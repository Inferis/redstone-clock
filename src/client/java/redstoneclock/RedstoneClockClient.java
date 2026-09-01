package redstoneclock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import redstoneclock.networking.OpenEditorS2CPayload;
import redstoneclock.screen.ClockScreen;

@Environment(EnvType.CLIENT)
public class RedstoneClockClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerPayloadReceivers();
	}

	private void registerPayloadReceivers() {
		RedstoneClock.LOGGER.info("client: registering network payload receivers");
		ClientPlayNetworking.registerGlobalReceiver(OpenEditorS2CPayload.ID, RedstoneClockClient::receiveOpenEditorPayload);
	}

	public static void receiveOpenEditorPayload(OpenEditorS2CPayload payload, ClientPlayNetworking.Context context) {
		context.client().gui.setScreen(new ClockScreen(payload));
	}
}
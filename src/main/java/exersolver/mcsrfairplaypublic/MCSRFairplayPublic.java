package exersolver.mcsrfairplaypublic;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCSRFairplayPublic implements ClientModInitializer {
	public static final String MOD_ID = "mcsrfairplay-public";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		InputListener.init();
	}
}
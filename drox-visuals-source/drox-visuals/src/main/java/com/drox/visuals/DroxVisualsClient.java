package com.drox.visuals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class DroxVisualsClient implements ClientModInitializer {
	public static final String MOD_ID = "drox_visuals";

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			HudEffects.tick(client);
			ParticleEffects.tick(client);
		});

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath(MOD_ID, "visuals"),
				(graphics, deltaTracker) -> HudEffects.render(graphics));
	}
}

package com.drox.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Ambient motes, speed trails and landing bursts. */
final class ParticleEffects {
	private static boolean wasOnGround = true;
	private static double lastVy;

	private ParticleEffects() {
	}

	static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		ClientLevel level = mc.level;
		if (player == null || level == null || mc.isPaused()) {
			return;
		}

		RandomSource rand = player.getRandom();
		Vec3 v = player.getDeltaMovement();

		// Ambient motes drifting around the player, themed per dimension.
		if (player.tickCount % 3 == 0) {
			ParticleOptions mote = ParticleTypes.END_ROD;
			if (level.dimension() == Level.NETHER) {
				mote = ParticleTypes.SMALL_FLAME;
			} else if (level.dimension() == Level.END) {
				mote = ParticleTypes.PORTAL;
			}
			double x = player.getX() + (rand.nextDouble() - 0.5) * 12.0;
			double y = player.getY() + rand.nextDouble() * 4.0;
			double z = player.getZ() + (rand.nextDouble() - 0.5) * 12.0;
			level.addParticle(mote, x, y, z, 0.0, 0.01 + rand.nextDouble() * 0.015, 0.0);
		}

		// Glowing trail behind the player while sprinting or gliding.
		if (player.isSprinting() || player.isFallFlying()) {
			for (int i = 0; i < 2; i++) {
				double x = player.getX() - v.x * 2.0 + (rand.nextDouble() - 0.5) * 0.4;
				double y = player.getY() + 0.2 + rand.nextDouble() * 1.4;
				double z = player.getZ() - v.z * 2.0 + (rand.nextDouble() - 0.5) * 0.4;
				level.addParticle(ParticleTypes.END_ROD, x, y, z, -v.x * 0.05, 0.0, -v.z * 0.05);
			}
		}

		// Ring of dust when landing from a fast fall.
		boolean onGround = player.onGround();
		if (onGround && !wasOnGround && lastVy < -0.6) {
			int count = 16;
			for (int i = 0; i < count; i++) {
				double angle = (Math.PI * 2.0 * i) / count;
				double dx = Math.cos(angle) * 0.18;
				double dz = Math.sin(angle) * 0.18;
				level.addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1, player.getZ(), dx, 0.02, dz);
			}
		}
		wasOnGround = onGround;
		lastVy = v.y;
	}
}

package com.drox.visuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

/** Animated HUD layer: accent bar, speed lines, vignette, damage pulse and dimension tint. */
final class HudEffects {
	private static float speedBlend;
	private static float hurt;
	private static float lastHealth = -1f;

	private HudEffects() {
	}

	/** Runs every client tick; smooths the values the renderer reads. */
	static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null) {
			speedBlend = 0f;
			hurt = 0f;
			lastHealth = -1f;
			return;
		}

		boolean fast = player.isSprinting() || player.isFallFlying();
		speedBlend += ((fast ? 1f : 0f) - speedBlend) * 0.15f;

		float health = player.getHealth();
		if (lastHealth >= 0f && health < lastHealth) {
			hurt = 1f;
		}
		lastHealth = health;
		hurt = Math.max(0f, hurt - 0.06f);
	}

	static void render(GuiGraphicsExtractor g) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || mc.level == null) {
			return;
		}

		int w = g.guiWidth();
		int h = g.guiHeight();
		float t = (float) (System.nanoTime() / 1_000_000_000.0 % 10_000.0);

		tint(g, w, h, mc);
		edges(g, w, h, 0x000000, 0.35f, 28);

		if (speedBlend > 0.02f) {
			speedLines(g, w, h, t);
		}

		float health = player.getHealth();
		if (health <= 6f && health > 0f) {
			float pulse = 0.5f + 0.5f * (float) Math.sin(t * 6f);
			edges(g, w, h, 0xC81414, 0.30f + 0.40f * pulse, 36);
		}
		if (hurt > 0.01f) {
			edges(g, w, h, 0xE01010, hurt * 0.8f, 48);
		}

		accentBar(g, w, h, t);
	}

	/** Moving rainbow strip just above the hotbar. */
	private static void accentBar(GuiGraphicsExtractor g, int w, int h, float t) {
		int width = 182;
		int x0 = (w - width) / 2;
		int y = h - 25;
		for (int x = 0; x < width; x += 2) {
			float f = x / (float) width;
			float edgeFade = 1f - Math.abs(f - 0.5f) * 2f;
			int alpha = (int) (210 * Math.min(1f, edgeFade * 2.5f));
			if (alpha <= 0) {
				continue;
			}
			int rgb = hsv((f + t * 0.15f) % 1f, 0.65f, 1f);
			g.fill(x0 + x, y, x0 + x + 2, y + 2, (alpha << 24) | rgb);
		}
	}

	/** Streaks flying in from the screen edges while sprinting or gliding. */
	private static void speedLines(GuiGraphicsExtractor g, int w, int h, float t) {
		for (int k = 0; k < 28; k++) {
			float rnd = frac(k * 0.61803f + 0.137f);
			float len = 24f + 90f * frac(k * 0.37f + 0.5f);
			float phase = (t * 1.6f + rnd * 3f) % 1f;
			int y = (int) (frac(k * 0.7548f) * h);
			int alpha = (int) (170f * speedBlend * (1f - phase));
			if (alpha <= 0) {
				continue;
			}
			int c = (alpha << 24) | 0xFFFFFF;
			int reach = (int) (len * (0.4f + 0.6f * phase));
			if ((k & 1) == 0) {
				g.fill(0, y, reach, y + 1, c);
			} else {
				g.fill(w - reach, y, w, y + 1, c);
			}
		}
	}

	/** Very light colour grade depending on the dimension. */
	private static void tint(GuiGraphicsExtractor g, int w, int h, Minecraft mc) {
		int rgb;
		int alpha;
		if (mc.level.dimension() == Level.NETHER) {
			rgb = 0xFF5A1E;
			alpha = 14;
		} else if (mc.level.dimension() == Level.END) {
			rgb = 0x8A3CFF;
			alpha = 14;
		} else {
			rgb = 0x3C78FF;
			alpha = 6;
		}
		g.fill(0, 0, w, h, (alpha << 24) | rgb);
	}

	/** Soft border that fades toward the centre, drawn as nested 1px frames. */
	private static void edges(GuiGraphicsExtractor g, int w, int h, int rgb, float strength, int size) {
		if (strength <= 0.01f) {
			return;
		}
		strength = Math.min(1f, strength);
		for (int i = 0; i < size; i++) {
			float f = 1f - i / (float) size;
			int a = (int) (255f * strength * f * f);
			if (a <= 0) {
				continue;
			}
			int c = (a << 24) | rgb;
			g.fill(i, i, w - i, i + 1, c);
			g.fill(i, h - i - 1, w - i, h - i, c);
			g.fill(i, i + 1, i + 1, h - i - 1, c);
			g.fill(w - i - 1, i + 1, w - i, h - i - 1, c);
		}
	}

	private static float frac(float v) {
		return v - (float) Math.floor(v);
	}

	private static int hsv(float hue, float sat, float val) {
		float hh = (hue % 1f) * 6f;
		int i = (int) Math.floor(hh);
		float f = hh - i;
		float p = val * (1f - sat);
		float q = val * (1f - sat * f);
		float u = val * (1f - sat * (1f - f));
		float r;
		float gr;
		float b;
		switch (i % 6) {
			case 0 -> { r = val; gr = u; b = p; }
			case 1 -> { r = q; gr = val; b = p; }
			case 2 -> { r = p; gr = val; b = u; }
			case 3 -> { r = p; gr = q; b = val; }
			case 4 -> { r = u; gr = p; b = val; }
			default -> { r = val; gr = p; b = q; }
		}
		return ((int) (r * 255f) << 16) | ((int) (gr * 255f) << 8) | (int) (b * 255f);
	}
}

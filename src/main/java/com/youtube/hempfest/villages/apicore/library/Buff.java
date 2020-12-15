package com.youtube.hempfest.villages.apicore.library;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Buff {
	FAST_DIG(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 60, 2)),
	HIGH_JUMP(new PotionEffect(PotionEffectType.JUMP, 20 * 60, 2)),
	MORE_LUCK(new PotionEffect(PotionEffectType.LUCK, 20 * 60, 1)),
	FULL_BRIGHT(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 120, 2)),
	LESS_DAMAGE(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 1));


	private final PotionEffect effect;

	Buff(PotionEffect effect) {
		this.effect = effect;
	}

	public PotionEffect getEffect() {
		return effect;
	}
}

package com.youtube.hempfest.villages.apicore.activities;

import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.Serializable;
import org.bukkit.potion.PotionEffect;

public class PotionBuff implements Serializable {

	private final PotionEffect effect;

	private final Village village;

	public PotionBuff(PotionEffect effect, Village village) {
		this.village = village;
		this.effect = effect;
		add();
	}

	private void add() {
		boolean replaced = false;
		for (PotionBuff buff : village.getBuffs()) {
			if (buff.getEffect().getType().equals(effect.getType())) {
				village.updateBuff(buff, this);
				replaced = true;
				break;
			}
		}
		if (!replaced) {
			village.addBuff(this);
		}
	}

	public PotionEffect getEffect() {
		return effect;
	}

}

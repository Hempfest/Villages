package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Village;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class AlarmProtect implements Listener {

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof Player) {
			Player p = (((Player) e.getTarget()).getPlayer());

			if (Clan.clanUtil.getClan(p) != null) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.getAlarm() != null) {
						if (v.getAlarm().distance(p.getLocation()) <= 40) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

}

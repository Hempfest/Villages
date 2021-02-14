package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Claim;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.events.ClaimResidentEvent;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.PotionBuff;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VillageAttachBuff implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void attachBuff(ClaimResidentEvent e) throws Exception {
			Player p = e.getResident().getPlayer();
			Claim c = e.getClaim();
			// Clan exists
			Callable<Village> village = () -> ClansVillages.getVillageById(ClansVillages.getVillageId(HempfestClans.clanManager(p)));
			Village v = village.call();
			if (v != null) {
				Clan clan = v.getOwner();
				// We own the claim.
				if (Arrays.asList(clan.getOwnedClaims()).contains(c.getClaimID())) {
					// Give them the buff.
					for (PotionBuff b : v.getBuffs()) {
						if (!p.hasPotionEffect(b.getEffect().getType())) {
							p.addPotionEffect(b.getEffect());
						}
					}
				}
				for (String ally : Clan.clanUtil.getAllies(clan.getClanID())) {
					Clan a = Clan.clanUtil.getClan(ally);
					if (Arrays.asList(a.getOwnedClaims()).contains(c.getClaimID())) {
						for (PotionBuff b : v.getBuffs()) {
							if (!p.hasPotionEffect(b.getEffect().getType())) {
								p.addPotionEffect(b.getEffect());
							}
						}
						break;
					}
				}
			}
	}

}

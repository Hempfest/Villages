package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Claim;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.events.ClaimResidentEvent;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.PotionBuff;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VillageAttachBuff implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void attachBuff(ClaimResidentEvent e) {
			Player p = e.getResident().getPlayer();
			Claim c = e.getClaim();
			// Clan exists
			Village v = null;
			for (Village village : ClansVillages.getVillages()) {
				if (village.isInhabitant(p.getName())) {
					v = village;
					break;
				}
			}
			if (v != null) {
				Clan clan = v.getOwner();
				// We own the claim.
				if (Arrays.asList(clan.getOwnedClaims()).contains(c.getClaimID())) {
					// Give them the buff.
					List<PotionBuff> give = v.getBuffs().stream().filter(b -> !p.hasPotionEffect(b.getEffect().getType())).collect(Collectors.toList());
					for (PotionBuff b : give) {
						p.addPotionEffect(b.getEffect());
					}
				}
				for (String ally : Clan.clanUtil.getAllies(clan.getClanID())) {
					Clan a = Clan.clanUtil.getClan(ally);
					if (Arrays.asList(a.getOwnedClaims()).contains(c.getClaimID())) {
						List<PotionBuff> give = v.getBuffs().stream().filter(b -> !p.hasPotionEffect(b.getEffect().getType())).collect(Collectors.toList());
						for (PotionBuff b : give) {
							p.addPotionEffect(b.getEffect());
						}
						break;
					}
				}
			}
	}

}

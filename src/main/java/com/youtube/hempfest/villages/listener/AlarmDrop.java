package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class AlarmDrop implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void alarmDrop(PlayerDropItemEvent e) {
		ItemStack i = e.getItemDrop().getItemStack();
		if (i.getItemMeta() != null) {
			if (i.getItemMeta().hasDisplayName()) {
				if (i.getItemMeta().getDisplayName().equals(Clan.clanUtil.color("&6&lVILLAGE ALARM"))) {
					Clan.clanUtil.sendMessage(e.getPlayer(), "&c&oYou cannot drop this item.");
					e.setCancelled(true);
				}
			}
		}
	}

}

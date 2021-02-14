package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class AlarmInventoryDrop implements Listener {

	@EventHandler
	public void alarmDrop(InventoryClickEvent e) {
		ItemStack i = e.getCurrentItem();
		if (!e.isCancelled()) {
			Player p = (Player) e.getWhoClicked();
			if (!(e.getInventory() instanceof CraftingInventory)) {
				if (p.getGameMode().equals(GameMode.SURVIVAL)) {
					if (i.getItemMeta() != null) {
						if (i.getItemMeta().hasDisplayName()) {
							if (i.getItemMeta().getDisplayName().equals(Clan.clanUtil.color("&6&lVILLAGE ALARM"))) {
								Clan.clanUtil.sendMessage((Player) e.getWhoClicked(), "&c&oYou cannot drop this item.");
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

}

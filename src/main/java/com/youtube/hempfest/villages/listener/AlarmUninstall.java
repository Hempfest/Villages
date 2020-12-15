package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.events.VillageAlarmDestructionEvent;
import com.youtube.hempfest.villages.events.VillageObjectiveLevelEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class AlarmUninstall implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void alarmUninstall(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (!e.isCancelled()) {
			Village a = null;
			try {
				a = ClansVillages.getVillageByAlarm(e.getBlock().getLocation());
			} catch (NullPointerException ignored) {
			}
			if (a != null) {
				Village owner = a;
				VillageAlarmDestructionEvent event = new VillageAlarmDestructionEvent(owner, e.getBlock().getLocation(), p);
				Bukkit.getPluginManager().callEvent(event);
				e.setCancelled(event.cantUninstall());
				if (!event.isCancelled()) {
					event.perform();
				}
				return;
			}
			Village v = null;
			for (Village village : ClansVillages.getVillages()) {
				if (village.isInhabitant(p.getName())) {
					v = village;
					break;
				}
			}
			if (v != null) {
				BlockData bdata = e.getBlock().getBlockData();
				if (bdata instanceof Ageable) {
					Ageable age = (Ageable) bdata;
					if (age.getAge() == age.getMaximumAge()) {
						if (!v.getObjective(16).isCompleted() || !v.getObjective(17).isCompleted()) {
							if (v.getInhabitant(p.getName()).getCurrentObjective() == 16) {
								Objective o = v.getObjective(16);
								if (o.completionPercentage() < 100.00) {
									VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, v.getInhabitant(p.getName()), o);
									Bukkit.getPluginManager().callEvent(event);
									o.addProgress(1);
									v.complete();
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b16&f) &b&l" + o.completionPercentage() + "&f% done.")));
								}
								if (o.completionPercentage() == 100.00) {
									o.setCompleted(true);
									v.getInhabitant(p.getName()).completed(16);
									v.getInhabitant(p.getName()).setObjective(0);
									v.complete();
									for (Inhabitant i : v.getInhabitants()) {
										if (i.getUser().isOnline()) {
											i.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lv &alevel up. &f(&a" + v.getLevel() + "&f)")));
											i.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
										}
									}
								}
							}
							if (v.getInhabitant(p.getName()).getCurrentObjective() == 17) {
								Objective o = v.getObjective(17);
								if (o.completionPercentage() < 100.00) {
									VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, v.getInhabitant(p.getName()), o);
									Bukkit.getPluginManager().callEvent(event);
									o.addProgress(1);
									v.complete();
									p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b17&f) &b&l" + o.completionPercentage() + "&f% done.")));
								}
								if (o.completionPercentage() == 100.00) {
									o.setCompleted(true);
									v.getInhabitant(p.getName()).completed(17);
									v.getInhabitant(p.getName()).setObjective(0);
									v.complete();
									for (Inhabitant i : v.getInhabitants()) {
										if (i.getUser().isOnline()) {
											i.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lv &alevel up. &f(&a" + v.getLevel() + "&f)")));
											i.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

}

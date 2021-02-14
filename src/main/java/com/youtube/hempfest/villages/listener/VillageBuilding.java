package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.events.VillageAlarmInstallationEvent;
import com.youtube.hempfest.villages.events.VillageCreationEvent;
import com.youtube.hempfest.villages.events.VillageObjectiveLevelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class VillageBuilding implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void villageBuilding(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!e.isCancelled()) {
			Village v = null;
			for (Village village : ClansVillages.getVillages()) {
				if (village.isInhabitant(p.getName())) {
					v = village;
					break;
				}
			}
			if (e.getItemInHand().getItemMeta() != null) {
				if (e.getItemInHand().getItemMeta().hasDisplayName()) {
					if (e.getItemInHand().getItemMeta().getDisplayName().equals(Clan.clanUtil.color("&6&lVILLAGE ALARM"))) {
						// create & save alarms location
						if (Clan.clanUtil.getClan(p) == null) {
							e.getItemInHand().setType(Material.AIR);
							Clan.clanUtil.sendMessage(p, "&c&oVillage features are reserved for members of clans.");
							e.setCancelled(true);
							return;
						}
						if (v == null) {
							if (Clan.clanUtil.getRankPower(p) >= 2) {
								v = new Village(Clan.clanUtil.getClan(p));
								v.setAlarmLoc(e.getBlock().getLocation());
								VillageCreationEvent ev = new VillageCreationEvent(v, p);
								Bukkit.getPluginManager().callEvent(ev);
								if (!ev.isCancelled()) {
									ev.perform();
								}
							} else {
								Clan.clanUtil.sendMessage(p, "&c&oYou do not have clan clearance.");
								e.setCancelled(true);
								return;
							}
						} else {
							Inhabitant i = v.getInhabitant(p.getName());
							VillageAlarmInstallationEvent event = new VillageAlarmInstallationEvent(v, i, e.getBlock().getLocation());
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled()) {
								event.perform();
								e.setCancelled(event.cantInstall());
							}
						}
						return;
					}
				}
			}
			if (Clan.clanUtil.getClan(p) == null) {
				return;
			}
			if (v != null) {
				List<Integer> prefix = new ArrayList<>(Arrays.asList(2, 3, 4));
				Inhabitant in = v.getInhabitant(p.getName());
				if (prefix.contains(in.getCurrentObjective())) {
					if (!v.getObjective(in.getCurrentObjective()).isCompleted()) {
						if (v.getInhabitant(p.getName()).hasPermission(Permission.LEVEL_OBJECTIVE)) {
							Objective o = v.getObjective(in.getCurrentObjective());
							if (o.completionPercentage() < 100) {
								VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, in, o);
								Bukkit.getPluginManager().callEvent(event);
								o.addProgress(1);
								v.complete();
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b" + in.getCurrentObjective() + "&f) &b&l" + o.completionPercentage() + "&f% done.")));
								return;
							}
							if (o.completionPercentage() == 100) {
								o.setCompleted(true);
								in.completed(in.getCurrentObjective());
								v.complete();
								v.sendMessage("&e&lObjective &f(&b" + in.getCurrentObjective() + "&f) &f{&3&lCOMPLETE&f}");
								in.setObjective(0);
								v.complete();
								for (Inhabitant i : v.getInhabitants()) {
									if (i.getUser().isOnline()) {
										i.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
										i.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
									}
								}
							}
						}
						return;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(1).equalsIgnoreCase("[Village]") && event.getLine(2).equalsIgnoreCase("Well")) {
			if (Clan.clanUtil.getClan(event.getPlayer()) != null) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(event.getPlayer().getName())) {
						v = village;
						break;
					}
				}
				if (v == null) {
					event.setLine(1, Clan.clanUtil.color("&f[&4&lVILLAGE&f]"));
					Clan.clanUtil.sendMessage(event.getPlayer(), "&c&oSign not created. Must be in a village.");
					return;
				}
				Inhabitant i = v.getInhabitant(event.getPlayer().getName());
				if (i.getCurrentObjective() == 5) {
					Objective o = v.getObjective(5);
					if (!o.isCompleted()) {
						if (o.completionPercentage() < 100.00) {
							VillageObjectiveLevelEvent e = new VillageObjectiveLevelEvent(v, i, o);
							Bukkit.getPluginManager().callEvent(e);
							o.addProgress(1);
							v.complete();
							event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b5&f) " + o.completionPercentage() + "&f% done.")));
						}
						if (o.completionPercentage() == 100.00) {
							o.setCompleted(true);
							i.completed(o.getLevel());
							i.setObjective(0);
							v.complete();
							v.sendMessage("&e&lObjective &f(&b5&f) &f{&3&lCOMPLETE&f}");
							for (Inhabitant in : v.getInhabitants()) {
								if (in.getUser().isOnline()) {
									in.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
									in.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
								}
							}
						}
					}
				}
				event.setLine(0, Clan.clanUtil.color("&f[&3&lVILLAGE&f]"));
				event.setLine(1, Clan.clanUtil.color("&bWater Hole"));
				event.setLine(2, Clan.clanUtil.color("&f&lBuilder:"));
				event.setLine(3, Clan.clanUtil.color(Clan.clanUtil.getClanTag(Clan.clanUtil.getClan(event.getPlayer()))));
				Clan.clanUtil.sendMessage(event.getPlayer(), "&6&oVault sign created.");
			} else {
				event.setLine(1, Clan.clanUtil.color("&f[&4&lVILLAGE&f]"));
				Clan.clanUtil.sendMessage(event.getPlayer(), "&c&oSign not created. Must be in a clan.");
			}
			return;
		}
	}

}

package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.events.PlayerPunchPlayerEvent;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.events.VillageObjectiveLevelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class AbilityUse implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDeathEvent e) {
		if (e.getEntity().getKiller() != null) {
			Player p = e.getEntity().getKiller();
			if (Clan.clanUtil.getClan(p) == null) {
				return;
			}
			if (p.getInventory().getItemInMainHand().hasItemMeta()) {
				ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
				if (meta.hasDisplayName()) {
					if (meta.getDisplayName().equals(Clan.clanUtil.color("&3&lTRUSTY VILLAGE TRIDENT"))) {
						Village v = null;
						for (Village village : ClansVillages.getVillages()) {
							if (village.isInhabitant(p.getName())) {
								v = village;
								break;
							}
						}
						if (v != null) {
							Inhabitant i = v.getInhabitant(p.getName());
							if (i.getCurrentObjective() == 6) {
								Objective o = v.getObjective(6);
								if (!o.isCompleted()) {
									if (o.completionPercentage() < 100.00) {
										VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, i, o);
										Bukkit.getPluginManager().callEvent(event);
										o.addProgress(1);
										v.complete();
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b6&f) " + o.completionPercentage() + "&f% done.")));
									}
									if (o.completionPercentage() == 100.00) {
										o.setCompleted(true);
										i.completed(o.getLevel());
										i.setObjective(0);
										v.sendMessage("&e&lObjective &f(&b6&f) &f{&3&lCOMPLETE&f}");
										for (Inhabitant in : v.getInhabitants()) {
											if (in.getUser().isOnline()) {
												in.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
												in.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			if (Clan.clanUtil.getClan(p) == null) {
				return;
			}
			if (p.getInventory().getItemInMainHand().hasItemMeta()) {
				ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
				if (meta.hasDisplayName()) {
					if (meta.getDisplayName().equals(Clan.clanUtil.color("&3&lTRUSTY VILLAGE TRIDENT"))) {
						Village v = null;
						for (Village village : ClansVillages.getVillages()) {
							if (village.isInhabitant(p.getName())) {
								v = village;
								break;
							}
						}
						if (v != null) {
							Inhabitant i = v.getInhabitant(p.getName());
							if (i.getCurrentObjective() == 6) {
								Objective o = v.getObjective(6);
								if (!o.isCompleted()) {
									if (o.completionPercentage() < 100.00) {
										e.setCancelled(true);
										if (e.getEntity() instanceof Monster) {
											Zombie z = (Zombie) e.getEntity();
											double health = z.getHealth();
											z.setHealth(health - 0.5);
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void swordUse(PlayerPunchPlayerEvent e) {
		Player p = e.getAttacker();
		Player t = e.getVictim();
		if (Clan.clanUtil.getClan(p) == null) {
			return;
		}
		Clan c = HempfestClans.clanManager(p);
		if (!Arrays.asList(c.getMembers()).contains(t.getName())) {
			if (Clan.clanUtil.getClan(t) != null) {
				Clan cl = HempfestClans.clanManager(t);
				if (Clan.clanUtil.getAllies(c.getClanID()).contains(cl.getClanID())) {
					return;
				}
			}
			if (p.getInventory().getItemInMainHand().hasItemMeta()) {
				ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
				if (meta.hasDisplayName()) {
					Village v = null;
					if (meta.getDisplayName().equals(Clan.clanUtil.color("&b&lWARRIOR'S BLADE OF JUDGEMENT"))) {
						for (Village village : ClansVillages.getVillages()) {
							if (village.isInhabitant(p.getName())) {
								v = village;
								break;
							}
						}
						if (v != null) {
							if (v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_WARRIOR)) {
								if (v.getInhabitant(p.getName()).getCurrentObjective() == 12) {
									if (!v.getObjective(v.getInhabitant(p.getName()).getCurrentObjective()).isCompleted()) {
										Objective o = v.getObjective(v.getInhabitant(p.getName()).getCurrentObjective());
										if (o.completionPercentage() < 100.00) {
											VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, v.getInhabitant(p.getName()), o);
											Bukkit.getPluginManager().callEvent(event);
											o.addProgress(1);
											v.complete();
											p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&6[&4Warrior&6] &e&lObjective &f(&b12&f) " + o.completionPercentage() + "&f% done.")));
										}
										if (o.completionPercentage() == 100.00) {
											v.getInhabitant(p.getName()).completed(o.getLevel());
											o.setCompleted(true);
											v.getInhabitant(p.getName()).setObjective(0);
											v.complete();
											v.sendMessage("&e&lObjective &f(&b" + o.getLevel() + "&f) &f{&3&lCOMPLETE&f}");
										}
									}
								}
								if (!((LivingEntity) p).isOnGround()) {
									double tH = t.getHealth();
									t.setHealth(tH - 1);
								}
							}
						}
					}
				}
			}
		}
	}

	private Entity getNearestEntityInSight(Player player, int range) {
		ArrayList<Entity> entities = (ArrayList<Entity>) player.getNearbyEntities(range, range, range);
		ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight(null, range);
		ArrayList<Location> sight = new ArrayList<>();
		int i;
		for (i = 0; i < sightBlock.size(); i++)
			sight.add(sightBlock.get(i).getLocation());
		for (i = 0; i < sight.size(); i++) {
			for (int k = 0; k < entities.size(); k++) {
				if (Math.abs(entities.get(k).getLocation().getX() - sight.get(i).getX()) < 1.3D &&
						Math.abs(entities.get(k).getLocation().getY() - sight.get(i).getY()) < 1.5D &&
						Math.abs(entities.get(k).getLocation().getZ() - sight.get(i).getZ()) < 1.3D)
					return entities.get(k);
			}
		}
		return null;
	}

	@EventHandler
	public void onBearFeed(PlayerInteractAtEntityEvent e) {

		Player p = e.getPlayer();

		if (e.getRightClicked() instanceof PolarBear) {

			if (p.getInventory().getItemInMainHand().getType() == Material.SALMON) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getObjective(7).isCompleted()) {
						Objective o = v.getObjective(7);
						if (v.getInhabitant(p.getName()).getCurrentObjective() == o.getLevel()) {
							if (o.completionPercentage() < 100.00) {
								VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, v.getInhabitant(p.getName()), o);
								Bukkit.getPluginManager().callEvent(event);
								o.addProgress(1);
								v.complete();
								p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b7&f) &b&l" + o.completionPercentage() + "&f% done.")));
							}

							if (o.completionPercentage() == 100.00) {
								o.setCompleted(true);
								v.getInhabitant(p.getName()).completed(7);
								v.getInhabitant(p.getName()).setObjective(0);
								v.sendMessage("&e&lObjective &f(&b7&f) &f{&3&lCOMPLETE&f}");
								v.complete();
								for (Inhabitant i : v.getInhabitants()) {
									if (i.getUser().isOnline()) {
										i.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
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

	@EventHandler(priority = EventPriority.LOW)
	public void scepterUse(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR) {
			Entity ent = getNearestEntityInSight(e.getPlayer(), 20);
			if (Clan.clanUtil.getClan(p) == null) {
				return;
			}
			if (ent instanceof Player) {
				Player target = (Player) ent;
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.isInhabitant(target.getName())) {
						if (v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_PRIEST)) {
							if (p.getInventory().getItemInMainHand().hasItemMeta()) {
								ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
								if (meta.hasDisplayName()) {
									if (meta.getDisplayName().equals(Clan.clanUtil.color("&f&lSCEPTER OF DIVINE FAITH"))) {
										if ((target.getHealth() + 1) < 20.00) {
											if (v.getInhabitant(p.getName()).getCurrentObjective() == 11) {
												if (!v.getObjective(v.getInhabitant(p.getName()).getCurrentObjective()).isCompleted()) {
													Objective o = v.getObjective(v.getInhabitant(p.getName()).getCurrentObjective());
													if (o.completionPercentage() < 100.00) {
														VillageObjectiveLevelEvent event = new VillageObjectiveLevelEvent(v, v.getInhabitant(p.getName()), o);
														Bukkit.getPluginManager().callEvent(event);
														o.addProgress(1);
														v.complete();
													}
													if (o.completionPercentage() == 100.00) {
														v.getInhabitant(p.getName()).completed(o.getLevel());
														o.setCompleted(true);
														v.getInhabitant(p.getName()).setObjective(0);
														v.complete();
														v.sendMessage("&e&lObjective &f(&b" + o.getLevel() + "&f) &f{&3&lCOMPLETE&f}");
													}
												}
											}
											target.getWorld().strikeLightningEffect(target.getLocation());
											double tH = target.getHealth();
											double y = 20.00;
											target.setHealth(tH + 1);
											p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&6[&f&lPRIEST&6] &aTarget has " + (Math.round(tH * 100 / y * 100.0) / 100.0) + "% health")));
											target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&6[&f&lPRIEST&6] &aYou have been healed.")));
										} else {
											p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&6[&f&lPRIEST&6] &c&oTarget has max health")));
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

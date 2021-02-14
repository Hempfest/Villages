package com.youtube.hempfest.villages.listener;

import com.google.common.collect.MapMaker;
import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.events.ClaimResidentEvent;
import com.youtube.hempfest.clans.util.events.CustomChatEvent;
import com.youtube.hempfest.clans.util.events.RaidShieldEvent;
import com.youtube.hempfest.clans.util.events.SubCommandEvent;
import com.youtube.hempfest.clans.util.events.TabInsertEvent;
import com.youtube.hempfest.clans.util.events.WildernessInhabitantEvent;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Permission;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VillageClans implements Listener {

	private final ConcurrentMap<Player, Boolean> titleSent = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	public String getEnterTitle() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.title-enter");
	}

	public String getEnterSub() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.sub-enter");
	}

	public String getExitTitle() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.title-exit");
	}

	public String getExitSub() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.sub-exit");
	}

	public boolean sendTitle() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getBoolean("village-near.send-title");
	}

	public String getEnterMessage() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.message-enter");
	}

	public String getExitMessage() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-near.message-exit");
	}

	@EventHandler
	public void onResident(ClaimResidentEvent e) throws Exception {
		Player p = e.getResident().getPlayer();
		if (ClansVillages.getInstance().getAllAlarms().size() == 0) {
			return;
		}
		for (Location a : ClansVillages.getInstance().getAllAlarms()) {
			if (a.distance(p.getLocation()) <= 100) {
				Callable<Village> village = () -> ClansVillages.getVillageByAlarm(a);
				Village v = village.call();
				if (!titleSent.containsKey(p) || !titleSent.get(p)) {
					titleSent.put(p, true);
					e.stringLibrary().sendMessage(p, String.format(getEnterMessage(), v.getOwner().getClanTag()));
					if (sendTitle()) {
						p.sendTitle(e.stringLibrary().color(String.format(getEnterTitle(), v.getOwner().getClanTag())), e.stringLibrary().color(String.format(getEnterSub(), v.getOwner().getClanTag())), 10, 25, 10);
					}
					break;
				}
			}
			if (a.distance(p.getLocation()) >= 100) {
				if (titleSent.containsKey(p) && titleSent.get(p)) {
					titleSent.put(p, false);
					e.stringLibrary().sendMessage(p, getExitMessage());
					if (sendTitle()) {
						p.sendTitle(e.stringLibrary().color(getExitTitle()), e.stringLibrary().color(getExitSub()), 10, 25, 10);
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void onWild(WildernessInhabitantEvent e) throws Exception {
		Player p = e.getPlayer();
		if (ClansVillages.getInstance().getAllAlarms().size() == 0) {
			return;
		}
		for (Location a : ClansVillages.getInstance().getAllAlarms()) {
			if (a.distance(p.getLocation()) <= 100) {
				Callable<Village> village = () -> ClansVillages.getVillageByAlarm(a);
				Village v = village.call();
				if (!titleSent.containsKey(p) || !titleSent.get(p)) {
					titleSent.put(p, true);
					e.stringLibrary().sendMessage(p, String.format(getEnterMessage(), v.getOwner().getClanTag()));
					if (sendTitle()) {
						p.sendTitle(e.stringLibrary().color(String.format(getEnterTitle(), v.getOwner().getClanTag())), e.stringLibrary().color(String.format(getEnterSub(), v.getOwner().getClanTag())), 10, 25, 10);
					}
					break;
				}
			}
			if (a.distance(p.getLocation()) >= 100) {
				if (titleSent.containsKey(p) && titleSent.get(p)) {
					titleSent.put(p, false);
					e.stringLibrary().sendMessage(p, getExitMessage());
					if (sendTitle()) {
						p.sendTitle(e.stringLibrary().color(getExitTitle()), e.stringLibrary().color(getExitSub()), 10, 25, 10);
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void onShield(RaidShieldEvent e) {
		if (e.shieldOn()) {
			for (Village v : ClansVillages.getVillages()) {
				if (v.getDailyPayment() > 0) {
					for (Inhabitant i : v.getInhabitants()) {
						if (i.getUser().isOnline()) {
							if (!i.hasPermission(Permission.ALL) && i.hasPermission(Permission.PAY_TO_STAY)) {
								if (!i.hasPayed() && !i.receivedAlert()) {
									i.setReceivedAlert(true);
									v.complete();
									Clan.clanUtil.sendMessage(i.getUser().getPlayer(), "&cYou haven't paid your daily rent. Not paying it within the next minute will will result in removal from the village.");
									Bukkit.getScheduler().scheduleSyncDelayedTask(ClansVillages.getInstance(), () -> {
										for (Village v2 : ClansVillages.getVillages()) {
											if (e.shieldOn()) {
												for (Inhabitant i2 : v2.getInhabitants()) {
													if (!i2.hasPermission(Permission.ALL) && i2.hasPermission(Permission.PAY_TO_STAY)) {
														if (i2.getUser().isOnline()) {
															if (!i2.hasPayed()) {
																v2.removeInhabitant(i2);
																v2.complete();
																ClansVillages.getInstance().getLogger().info("- Removing inhabitant " + i2.getUser().getName() + " from their village for not paying rent.");
																break;
															}
														}
													}
												}
											}
										}
									}, 20 * 60);
									break;
								}
								if (i.hasPayed() && !i.receivedAlert()) {
									i.setHasPayed(false);
									i.setReceivedAlert(true);
									v.complete();
								}
							}
						} else {
							if (!i.hasPayed()) {
								if (i.receivedAlert()) {
									i.setReceivedAlert(false);
									v.complete();
									break;
								}
							}
							if (i.hasPayed()) {
								if (!i.receivedAlert()) {
									i.setReceivedAlert(true);
									v.complete();
									break;
								}
							}
						}
					}
				}
			}
		} else {
			for (Village v : ClansVillages.getVillages()) {
				if (v.getDailyPayment() > 0) {
					for (Inhabitant i : v.getInhabitants()) {
						if (i.getUser().isOnline()) {
							if (!i.hasPermission(Permission.ALL) && i.hasPermission(Permission.PAY_TO_STAY)) {
								if (i.receivedAlert()) {
									i.setHasPayed(false);
									i.setReceivedAlert(false);
									v.complete();
									break;
								}
							}
						} else {
							if (!i.hasPermission(Permission.ALL) && i.hasPermission(Permission.PAY_TO_STAY)) {
								if (i.hasPermission(Permission.TAX_ON_ABSENCE)) {
									if (!i.hasPayed()) {
										if (!i.receivedAlert()) {
											i.setReceivedAlert(true);
											i.addTax(v.getLateTax());
											v.complete();
											ClansVillages.getInstance().getLogger().info("- Inhabitant " + i.getUser().getName() + " of village " + v.getOwner().getClanTag() + " interest increase x " + v.getLateTax());
											break;
										}
									}
									if (i.hasPayed()) {
										if (i.receivedAlert()) {
											i.setReceivedAlert(false);
											i.addTax(v.getLateTax());
											v.complete();
											ClansVillages.getInstance().getLogger().info("- Inhabitant " + i.getUser().getName() + " of village " + v.getOwner().getClanTag() + " interest increase x " + v.getLateTax());
											break;
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


	@EventHandler
	public void bankChat(CustomChatEvent e) {
		if (e.getChannel().equals("village_bankers")) {
			e.setPrefix("&2(&aBank&2) &7" + e.getChatting().getName() + " &r");
			if (Bukkit.getVersion().contains("1.16")) {
				e.setDivider("&#3b3b3b&o» &f");
			} else {
				e.setDivider("&8&o» &r");
			}
			if (Clan.clanUtil.getClan(e.getChatting()) != null) {
				e.setHighlight("&f&o[&b" + HempfestClans.clanManager(e.getChatting()).getClanTag() + "&f&o] ");
				e.setHoverMeta("&b&oI have &7&o" + HempfestClans.clanManager(e.getChatting()).getPower() + " &b&oclan power.");
			} else {
				e.setHighlight("&7&o[&cNone&7&o]");
				e.setHoverMeta("&7I'm not a member of any clan.");
			}
			e.setPingSound(Sound.ENTITY_VILLAGER_TRADE);
		}
	}

	@EventHandler
	public void onTabInsert(TabInsertEvent e) {
		List<String> tab2 = new ArrayList<>(Arrays.asList("bank"));
		String[] args = e.getCommandArgs();

		if (args[0].equalsIgnoreCase("chat")) {
			for (String t : tab2) {
				if (!e.getArgs(2).contains(t)) {
					e.add(2, t);
				}
			}
		}
	}

	@EventHandler
	public void onSub(SubCommandEvent e) {
		String[] args = e.getArgs();

		if (args.length == 1) {
			if (e.getArgs()[0].equalsIgnoreCase("leave")) {
				if (Clan.clanUtil.getClan(e.getSender()) != null) {
					Clan.clanUtil.leave(e.getSender());
					ClansVillages.loadVillages();
					e.setReturn(true);
				}
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("chat")) {
				if (args[1].equalsIgnoreCase("bank")) {
					if (HempfestClans.chatMode.get(e.getSender()).equals("village_bankers")) {
						HempfestClans.chatMode.put(e.getSender(), "GLOBAL");
						e.stringLibrary().sendMessage(e.getSender(), "&7&oSwitched to &3CLAN &7&ochat channel.");
					} else {
						HempfestClans.chatMode.put(e.getSender(), "village_bankers");
						e.stringLibrary().sendMessage(e.getSender(), "&7&oSwitched to &6BANK &7&ochat channel.");
					}
					e.setReturn(true);
				}
			}
		}
	}


}

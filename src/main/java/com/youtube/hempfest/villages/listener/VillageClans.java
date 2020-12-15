package com.youtube.hempfest.villages.listener;

import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.events.RaidShieldEvent;
import com.youtube.hempfest.clans.util.events.SubCommandEvent;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VillageClans implements Listener {

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
	public void onSub(SubCommandEvent e) {
		if (e.getArgs().length == 1) {
			if (e.getArgs()[0].equalsIgnoreCase("leave")) {
				if (Clan.clanUtil.getClan(e.getSender()) != null) {
					Clan.clanUtil.leave(e.getSender());
					ClansVillages.loadVillages();
					e.setReturn(true);
				}
			}
		}
	}


}

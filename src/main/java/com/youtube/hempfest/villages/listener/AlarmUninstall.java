package com.youtube.hempfest.villages.listener;

import com.github.sanctum.labyrinth.formatting.string.ColoredString;
import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.events.VillageAlarmDestructionEvent;
import com.youtube.hempfest.villages.events.VillageObjectiveLevelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AlarmUninstall implements Listener {

	private List<String> color(String... text) {
		ArrayList<String> convert = new ArrayList<>();
		for (String t : text) {
			convert.add(new ColoredString(t, ColoredString.ColorType.MC).toString());
		}
		return convert;
	}


	private ItemStack makePersistentItem(Material material, String displayName, String key, String data, String... lore) {

		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		assert itemMeta != null;
		itemMeta.setDisplayName(new ColoredString(displayName, ColoredString.ColorType.MC).toString());
		itemMeta.getPersistentDataContainer().set(new NamespacedKey(ClansVillages.getInstance(), key), PersistentDataType.STRING, data);
		itemMeta.setLore(color(lore));
		item.setItemMeta(itemMeta);

		return item;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void alarmUninstall(BlockBreakEvent e) throws Exception {
		Player p = e.getPlayer();
		if (!e.isCancelled()) {
			Village a = null;
			try {
				Callable<Village> vill = () -> ClansVillages.getVillageByAlarm(e.getBlock().getLocation());
				a = vill.call();
			} catch (NullPointerException ignored) {
			}
			if (a != null) {

				VillageAlarmDestructionEvent event = new VillageAlarmDestructionEvent(a, e.getBlock().getLocation(), p);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.cantUninstall()) {
					ItemStack bell = makePersistentItem(Material.BELL, "&c&o" + a.getOwner().getClanTag() + "'s village alarm.", "alarm", a.getOwner().getClanID(), "");
					bell.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
					e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0, 1, 0), bell);
					e.getBlock().setType(Material.AIR);
				}
				e.setCancelled(true);

				if (!event.isCancelled()) {
					event.perform();
				}
				return;
			}
			try {
				Callable<Village> village = () -> ClansVillages.getVillageById(ClansVillages.getVillageId(HempfestClans.clanManager(p)));
				Village v = village.call();
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
			} catch (NullPointerException ignored) {

			}
		}
	}

}

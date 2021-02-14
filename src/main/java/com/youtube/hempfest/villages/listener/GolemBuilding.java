package com.youtube.hempfest.villages.listener;

import com.google.common.collect.MapMaker;
import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.events.VillageObjectiveLevelEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class GolemBuilding implements Listener {

	// Don't keep a reference to either the Player nor the Location
	private final ConcurrentMap<Player, Location> lastPlaced = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPace(BlockPlaceEvent event) throws Exception {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		// Save the location of the last placed block
		try {
			Callable<Village> village = () -> ClansVillages.getVillageById(ClansVillages.getVillageId(HempfestClans.clanManager(player)));
			Village v = village.call();
			if (v != null) {
				lastPlaced.put(player, location);
			}
		} catch (NullPointerException ignored) {

		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

		// Make sure this creature was "built"
		if (reason == CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM) {
			Player responsible = findNearestPlayerPlacedBlock(event.getLocation());

			if (responsible != null) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(responsible.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					Inhabitant i = v.getInhabitant(responsible.getName());
					if (!v.getObjective(18).isCompleted()) {
						if (i.getCurrentObjective() == 18) {
							Objective o = v.getObjective(18);
							if (o.completionPercentage() < 100.00) {
								VillageObjectiveLevelEvent e = new VillageObjectiveLevelEvent(v, i, o);
								Bukkit.getPluginManager().callEvent(e);
								o.addProgress(1);
								v.complete();
								responsible.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b" + i.getCurrentObjective() + "&f) &b&l" + o.completionPercentage() + "&f% done.")));
							}
							if (o.completionPercentage() == 100.00) {
								o.setCompleted(true);
								i.completed(18);
								i.setObjective(0);
								v.complete();
								v.sendMessage("&e&lObjective &f(&b18&f) &f{&3&lCOMPLETE&f}");
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

	private Player findNearestPlayerPlacedBlock(Location location) {
		Player best = null;
		double bestDistance = Double.MAX_VALUE;

		// Simple linear search
		for (Player player : location.getWorld().getPlayers()) {
			Location lastPlacedBlock = lastPlaced.get(player);

			if (lastPlacedBlock != null) {
				double distance = location.distanceSquared(lastPlacedBlock);

				if (distance < bestDistance && distance < 10) {
					best = player;
					distance = bestDistance;
				}
			}
		}
		return best;
	}

}

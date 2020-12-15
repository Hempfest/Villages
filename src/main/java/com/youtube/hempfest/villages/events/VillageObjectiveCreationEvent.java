package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageObjectiveCreationEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final List<Objective> newInserts = new ArrayList<>();

	private boolean cancelled;

	private final Village village;

	public VillageObjectiveCreationEvent(Village village) {
		this.village = village;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public ClanUtil getUtil() {
		return Clan.clanUtil;
	}

	@Override
	public StringLibrary stringLibrary() {
		return Clan.clanUtil;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public Village getVillage() {
		return village;
	}

	public void addObjective(Objective objective) {
		boolean stop = false;
		for (int i = 1; i < village.getObjectives().size() + 1 ; i++) {
			if (village.getObjective(i).getLevel() == objective.getLevel()) {
				stop = true;
				break;
			}
		}
		if (!stop) {
			newInserts.add(objective);
			ClansVillages.getInstance().getLogger().info("- Inserted an objective with id #" + objective.getLevel());
		} else {
			ClansVillages.getInstance().getLogger().warning("- Could not add objective level #" + objective.getLevel() + "");
			ClansVillages.getInstance().getLogger().warning("- An objective with this level already exists.");
		}
	}

	public List<Objective> getNewInserts() {
		return newInserts;
	}

}

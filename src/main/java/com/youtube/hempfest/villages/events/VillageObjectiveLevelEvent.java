package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageObjectiveLevelEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Inhabitant leveler;

	private final Objective o;

	public VillageObjectiveLevelEvent(Village village, Inhabitant leveler, Objective o) {
		this.village = village;
		this.leveler = leveler;
		this.o = o;
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

	public Inhabitant getInhabitant() {
		return leveler;
	}

	public Objective getObjective() {
		return o;
	}

}

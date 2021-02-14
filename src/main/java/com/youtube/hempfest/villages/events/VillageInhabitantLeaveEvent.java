package com.youtube.hempfest.villages.events;

import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Position;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageInhabitantLeaveEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player leaving;

	private final Message msg;

	public VillageInhabitantLeaveEvent(Village village, Player leaving, Message msg) {
		this.village = village;
		this.leaving = leaving;
		this.msg = msg;
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
		return village.getInhabitant(leaving.getName());
	}

	public void perform() {
		Inhabitant i = getInhabitant();
		if (i.hasRole(Position.VILLAGE_CHIEF)) {
			msg.send("&7&oNice try chief. Buf if you want to throw away your progress. Use &c&o/v disband");
			return;
		}
		village.removeInhabitant(i);
		village.complete();
		village.sendMessage("&c&o" + leaving.getName() + " &7&ono longer partakes inhabitancy of the village.");
	}

}

package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Position;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageDeletionEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player deleter;

	public VillageDeletionEvent(Village village, Player deleter) {
		this.village = village;
		this.deleter = deleter;
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

	public Inhabitant getDeleter() {
		return village.getInhabitant(deleter.getName());
	}

	public void perform() {
		if (village.getInhabitant(deleter.getName()).hasRole(Position.VILLAGE_CHIEF)) {
			Bukkit.broadcastMessage(stringLibrary().color(String.format(getBroadcast(), village.getOwner().getClanTag())));
			ClansVillages.deleteVillageByMetaId(village.getOwner().getId(425));
		}
	}

	public String getBroadcast() {
		Config data = Config.get("Messages", "Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-delete");
	}

}

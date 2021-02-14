package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.InputStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageCreationEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player creator;

	public VillageCreationEvent(Village village, Player creator) {
		this.village = village;
		this.creator = creator;
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

	public Inhabitant getCreator() {
		return village.getInhabitant(creator.getName());
	}

	public void perform() {
		village.usedAlarm(true);
		village.getRole(Position.VILLAGE_CHIEF).addPermission(Permission.ALL);
		village.getRole(Position.VILLAGE_WARDEN).addPermission(Permission.KICK_INHABITANT);
		village.getRole(Position.VILLAGE_WARDEN).addPermission(Permission.INVITE_PLAYER);
		village.getRole(Position.VILLAGE_WARDEN).addPermission(Permission.UPDATE_HALL);
		village.getRole(Position.VILLAGE_WARDEN).addPermission(Permission.USE_HALL);
		village.getRole(Position.VILLAGE_PRIEST).addPermission(Permission.USE_HALL);
		village.getRole(Position.VILLAGE_WARRIOR).addPermission(Permission.USE_HALL);
		village.getRole(Position.VILLAGE_MEMBER).addPermission(Permission.USE_HALL);
		village.getRole(Position.VILLAGE_MEMBER).addPermission(Permission.LEVEL_OBJECTIVE);
		Inhabitant i = new Inhabitant(creator.getUniqueId(), village, HempfestClans.clanManager(creator));
		i.completed(1);
		i.giveRole(Position.VILLAGE_CHIEF);
		Objective o = village.getObjective(1);
		o.addProgress(1);
		if (o.completionPercentage() == 100) {
			o.setCompleted(true);
		}
		village.complete();
		Bukkit.broadcastMessage(Clan.clanUtil.color(String.format(getBroadcast(), creator.getName(), Clan.clanUtil.getColor(village.getOwner().getChatColor()) + village.getOwner().getClanTag())));
	}

	public String getBroadcast() {
		Config data = Config.get("Messages", "Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("village-create");
	}

}

package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.InputStream;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageAlarmInstallationEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Inhabitant inhabitant;

	private final Location alarmLoc;

	private boolean canInstall;

	public VillageAlarmInstallationEvent(Village village, Inhabitant inhabitant, Location alarmLoc) {
		this.village = village;
		this.inhabitant = inhabitant;
		this.alarmLoc = alarmLoc;
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

	public boolean cantInstall() {
		return canInstall;
	}

	public void allowInstall(boolean b) {
		this.canInstall = !b;
	}

	public Inhabitant getInhabitant() {
		return inhabitant;
	}

	public Village getVillage() {
		return village;
	}

	public Location getLocation() {
		return alarmLoc;
	}

	public void perform() {
		if (village.getAlarm() != null) {
			stringLibrary().sendMessage(inhabitant.getUser().getPlayer(), getDeny());
			allowInstall(false);
			return;
		}
		village.setAlarmLoc(alarmLoc);
		village.complete();
		village.sendMessage(String.format(getMessage(), inhabitant.getUser().getName()));
	}

	public String getDeny() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("alarm-deny");
	}

	public String getMessage() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("alarm-install");
	}

}

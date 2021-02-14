package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import java.io.InputStream;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageAlarmDestructionEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player destroyer;

	private final Location loc;

	private boolean canUninstall;

	public VillageAlarmDestructionEvent(Village village, Location loc, Player destroyer) {
		this.village = village;
		this.loc = loc;
		this.destroyer = destroyer;
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

	public Village getDestroyerVillage() {
		Village v = null;
		for (Village village : ClansVillages.getVillages()) {
			if (village.isInhabitant(destroyer.getName())) {
				v = village;
				break;
			}
		}
		return v;
	}

	public Player getDestroyer() {
		return destroyer;
	}

	public Location getLocation() {
		return loc;
	}

	public boolean cantUninstall() {
		return canUninstall;
	}

	public void allowUninstall(boolean b) {
		this.canUninstall = !b;
	}

	public void perform() {
			if (getDestroyerVillage() != null) {
				if (!getDestroyerVillage().getAlarm().equals(village.getAlarm())) {
					if (village.isInhabitant(destroyer.getName())) {
						if (!village.getObjective(10).isCompleted()) {
							Objective o = village.getObjective(10);
							if (village.getInhabitant(destroyer.getName()).getCurrentObjective() == 10) {
								if (o.completionPercentage() < 100.00) {
									o.addProgress(1);
									village.complete();
									destroyer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b10&f) &b&l" + o.completionPercentage() + "&f% done.")));
									o.setCompleted(true);
									village.getInhabitant(destroyer.getName()).completed(10);
									village.getInhabitant(destroyer.getName()).setObjective(0);
									village.complete();
									for (Inhabitant i : village.getInhabitants()) {
										if (i.getUser().isOnline()) {
											i.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + village.getLevel() + "&f)")));
											i.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
										}
									}
								}
							}
						}
					}
					getDestroyerVillage().getInhabitant(destroyer.getName()).destroyed(1);
					getDestroyerVillage().complete();
					getDestroyerVillage().sendMessage("&6&l" + destroyer.getName() + " &c&oHAS DESTROYED ANOTHER VILLAGES ALARM.");
				}
			}
			ClansVillages.removeAlarm(village.getAlarm());
			village.setAlarmLoc(null);
			village.complete();
			village.sendMessage(String.format(getMessage(), destroyer.getName()));
	}

	public String getMessage() {
		Config data = Config.get("Messages", "Configuration/Villages");
		if (!data.exists()) {
			InputStream is = ClansVillages.getInstance().getResource("Messages.yml");
			Config.copy(is, data.getFile());
		}
		return data.getConfig().getString("alarm-uninstall");
	}

}

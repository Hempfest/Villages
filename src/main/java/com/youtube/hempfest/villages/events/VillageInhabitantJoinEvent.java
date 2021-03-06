package com.youtube.hempfest.villages.events;

import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Invitation;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Village;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageInhabitantJoinEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player invited;

	public VillageInhabitantJoinEvent(Village village, Player invited) {
		this.village = village;
		this.invited = invited;
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
		return village.getInhabitant(invited.getName());
	}

	public Invitation getInvite() {
		return village.getInvite(invited);
	}

	public void perform() {
		if (!village.getObjective(8).isCompleted()) {
			if (village.getInhabitant(getInvite().getInviter().getName()).getCurrentObjective() == 8) {
				Objective o = village.getObjective(8);
				if (o.completionPercentage() < 100.00) {
					o.addProgress(1);
					village.complete();
					if (getInvite().getInviter().isOnline()) {
						getInvite().getInviter().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b8&f) &b&l" + o.completionPercentage() + "&f% done.")));
					}
				}
				if (o.completionPercentage() == 100.00) {
					o.setCompleted(true);
					village.getInhabitant(getInvite().getInviter().getName()).completed(8);
					village.getInhabitant(getInvite().getInviter().getName()).setObjective(0);
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
		village.acceptInvite(getInvite());
		village.complete();
		village.sendMessage("&a&oPlayer " + invited.getName() + " is now an inhabitant of our village.");
		Bukkit.dispatchCommand(invited, "v motd");
	}

}

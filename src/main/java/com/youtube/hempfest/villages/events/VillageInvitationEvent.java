package com.youtube.hempfest.villages.events;

import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.clans.util.StringLibrary;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.construct.ClanUtil;
import com.youtube.hempfest.clans.util.listener.ClanEventBuilder;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Invitation;
import com.youtube.hempfest.villages.apicore.entities.Village;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class VillageInvitationEvent extends ClanEventBuilder implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;

	private final Village village;

	private final Player inviter;

	private final Player invited;

	private Message msg;

	public VillageInvitationEvent(Village village, Player inviter, Player invited, Message msg) {
		this.village = village;
		this.inviter = inviter;
		this.invited = invited;
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

	public Player getInviter() {
		return inviter;
	}

	public Player getInvited() {
		return invited;
	}

	public boolean invitationSuccess() {
		return getInvite() != null;
	}

	public Invitation getInvite() {
		try {
			return village.getInvite(invited);
		} catch (NullPointerException e) {
			ClansVillages.getInstance().getLogger().severe("- Invitation was not found. Ensure proper checks before accessing.");
			return null;
		}
	}

	public void perform() {
		if (village.isInhabitant(invited)) {
			msg.send("&c&oPlayer " + invited.getName() + " is already a village inhabitant.");
			return;
		}
		if (village.isInvited(invited)) {
			msg.send("&c&oPlayer " + invited.getName() + " is already invited to our village. Awaiting a response.");
			return;
		}
		village.invitePlayer(inviter.getUniqueId(), invited.getUniqueId());
		msg.send("&a&oPlayer " + invited.getName() + " has been invited.");
		msg = new Message(invited, Clan.clanUtil.getPrefix());
		msg.send("&6&oYou have been invited to become a member of our village.");
		msg.send("&7&oType &f/village accept " + Clan.clanUtil.getColor(village.getOwner().getChatColor()) + village.getOwner().getClanTag());
		msg.send("&7&oor &f/village deny " + Clan.clanUtil.getColor(village.getOwner().getChatColor()) + village.getOwner().getClanTag());
		village.complete();
	}

}

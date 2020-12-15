package com.youtube.hempfest.villages.apicore.activities;

import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.Serializable;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Invitation implements Serializable {

	private final UUID inviter;

	private final UUID invited;

	private final Village village;

	public Invitation(UUID inviter, UUID invited, Village village) {
		this.inviter = inviter;
		this.invited = invited;
		this.village = village;
	}

	public OfflinePlayer getInvited() {
		return Bukkit.getOfflinePlayer(invited);
	}

	public OfflinePlayer getInviter() {
		return Bukkit.getOfflinePlayer(inviter);
	}

	public Village getVillage() {
		return village;
	}


}

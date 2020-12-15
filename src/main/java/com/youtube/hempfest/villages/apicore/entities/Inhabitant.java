package com.youtube.hempfest.villages.apicore.entities;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.metadata.ClanMeta;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.apicore.permissive.Inheritance;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Inhabitant extends Inheritance implements Serializable {

	private final List<Permission> userPerms = new ArrayList<>();

	private final List<Position> userRoles = new ArrayList<>();

	private final UUID user;

	private final Village village;

	private final Clan clan;

	private final Date timeJoined;

	private final List<Integer> completedObjectives = new ArrayList<>();

	private int objective;

	private int alarmsDestroyed;

	private boolean hasPayed;

	private double taxOwed;

	private boolean receivedAlert;

	public Inhabitant(UUID user, Village village, Clan clan) {
		this.user = user;
		this.clan = clan;
		village.addInhabitant(this);
		this.village = village;
		this.timeJoined = new Date();
	}

	@Override
	public List<Permission> getPermissions() {
		List<Permission> array = new ArrayList<>(userPerms);
		for (Position r : userRoles) {
			village.getRole(r).getPermissions().forEach(p -> {
				if (!array.contains(p)) {
					array.add(p);
				}
			});
		}
		return array;
	}

	@Override
	public List<Position> getRoles() {
		return userRoles;
	}

	public Clan getClan() {
		if (getUser().isOnline()) {
			return HempfestClans.clanManager(Bukkit.getPlayer(user));
		}
		return clan;
	}

	public Role getPrimaryRole() {
		int pri = 0;
		for (Position p : getRoles()) {
			if (village.getRole(p).getPriority() > pri) {
				pri = village.getRole(p).getPriority();
			}
		}
		return village.getRole(pri);
	}

	public boolean hasPayed() {
		return hasPayed;
	}

	public boolean receivedAlert() {
		return receivedAlert;
	}

	@Override
	public ClanMeta villageMeta() {
		return village.getMeta();
	}

	@Override
	public Village village() {
		return village;
	}

	public Date getJoinDate() {
		return timeJoined;
	}

	public void givePermission(Permission permission) {
		userPerms.add(permission);
	}

	public void takePermission(Permission permission) {
		userPerms.remove(permission);
	}

	public void giveRole(Position role) {
		userRoles.add(role);
	}

	public void takeRole(Position role) {
		userRoles.remove(role);
	}

	public OfflinePlayer getUser() {
		return Bukkit.getOfflinePlayer(user);
	}

	public void setHasPayed(boolean hasPayed) {
		this.hasPayed = hasPayed;
	}

	public void addTax(double amount) {
		this.taxOwed += amount;
	}

	public double getTaxOwed() {
		return taxOwed;
	}

	public void setReceivedAlert(boolean hasReceived) {
		this.receivedAlert = hasReceived;
	}

	public void setObjective(int objLevel) {
		this.objective = objLevel;
	}

	public int getCurrentObjective() {
		return objective;
	}

	public void completed(int objective) {
		completedObjectives.add(1);
	}

	public void destroyed(int amount) {
		this.alarmsDestroyed += amount;
	}

	public int getCompletedObjectives() {
		return completedObjectives.size();
	}

	public int getAlarmsDestroyed() {
		return alarmsDestroyed;
	}


}

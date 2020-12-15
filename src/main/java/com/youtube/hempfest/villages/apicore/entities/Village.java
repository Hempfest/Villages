package com.youtube.hempfest.villages.apicore.entities;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.metadata.ClanMeta;
import com.youtube.hempfest.clans.metadata.PersistentClan;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.data.ConfigType;
import com.youtube.hempfest.clans.util.data.DataManager;
import com.youtube.hempfest.hempcore.library.HUID;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Invitation;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.activities.PotionBuff;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.events.VillageObjectiveCreationEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Village implements Serializable {

	private final HUID id;

	private final UUID uuid;

	private final Village instance;

	private final String clanID;

	private final List<Inhabitant> inhabitants = new ArrayList<>();

	private final List<Role> roles = new ArrayList<>();

	private final List<Invitation> invitations = new ArrayList<>();

	private final List<Objective> objectives = new ArrayList<>();

	private final List<PotionBuff> buffs = new ArrayList<>();

	private Location alarmLoc;

	private Location outpostLoc;

	private Location hallLoc;

	private boolean usedAlarm;

	private double dailyPayment;

	private double villageBank;

	private double lateTax = 0.01;

	public Village(String clanID) {
		this.id = HUID.randomID();
		this.uuid = UUID.randomUUID();
		this.clanID = clanID;
		this.instance = this;
		Role ra = new Role(Position.VILLAGE_MEMBER, 1, this);
		Role rb = new Role(Position.VILLAGE_WARRIOR, 2, this);
		Role rc = new Role(Position.VILLAGE_PRIEST, 3, this);
		Role rd = new Role(Position.VILLAGE_WARDEN, 4, this);
		Role re = new Role(Position.VILLAGE_CHIEF, 5, this);
		roles.addAll(Arrays.asList(ra, rb, rc, rd, re));
		Objective a = new Objective(1, 1, "Awake and alert.", "Install the village alarm", this);
		Objective b = new Objective(2, 150, "Take watch!", "Build an outpost", this);
		Objective c = new Objective(3, 96, "It's time to talk.", "Build a village hall", this);
		Objective d = new Objective(4, 432, "Protecting the innocent!", "Build a wall", this);
		Objective e = new Objective(5, 1, "Quench the thirst.", "Build a well", this);
		Objective f = new Objective(6, 25, "Destroy the horde!", "Kill 25 zombies with the village trident", this);
		Objective g = new Objective(7, 1, "Dont hurt me!", "Feed a polar bear salmon", this);
		Objective h = new Objective(8, 10, "At large we stand.", "Grow your village population to 10 inhabitants", this);
		Objective i = new Objective(9, 1, "Climbing the ranks.", "Promote an inhabitant to village warden.", this);
		Objective j = new Objective(10, 1, "Stealthy compromise.", "Destroy another villages alarm system.", this);
		Objective k = new Objective(11, 2, "To the rescue!", "Heal another playing in combat with a scepter of faith.", this);
		Objective l = new Objective(12, 5, "Feel the power!", "Down strike at-least 5 enemy player's with the blade of judgement.", this);
		Objective m = new Objective(13, 1, "Healthy assignment.", "Appoint the role priest to a village inhabitant.", this);
		Objective n = new Objective(14, 1, "Fixation of lethal force", "Assign the warrior role to a village inhabitant.", this);
		Objective o = new Objective(15, 530, "Size matters!","Expand your village wall parameters.", this);
		Objective p = new Objective(16, 14, "Feed the people.","Harvest melons to feed inhabitants with.", this);
		Objective q = new Objective(17, 14, "Feed the people pt. II","Harvest wheat to feed inhabitants with", this);
		Objective r = new Objective(18, 4, "Titans of olympus!","Build 4 Iron Golem's for your village.", this);
		objectives.addAll(Arrays.asList(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r));
		VillageObjectiveCreationEvent event = new VillageObjectiveCreationEvent(instance);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			objectives.addAll(event.getNewInserts());
		}
	}

	/** ================ [ VILLAGE MISC ] ================= */

	public HUID getId() {
		return instance.id;
	}

	public int getLevel() {
		int level = 0;
		for (int i = 1; i < objectives.size() + 1; i++) {
			if (instance.getObjective(i).isCompleted()) {
				level += instance.getObjective(i).getLevel();
			}
		}
		return level;
	}

	public void setDailyPayment(double amount) {
		instance.dailyPayment = amount;
	}

	public double getDailyPayment() {
		return instance.dailyPayment;
	}

	public double getLateTax() {
		return instance.lateTax;
	}

	public void setLateTax(double lateTax) {
		instance.lateTax = lateTax;
	}

	public void giveMoney(double amount) {
		instance.villageBank += amount;
	}

	public void takeMoney(double amount) {
		instance.villageBank -= amount;
	}

	public double getVillageBankBalance() {
		return instance.villageBank;
	}

	public UUID getUniqueId() {
		return instance.uuid;
	}

	public Clan getOwner() {
		return Clan.clanUtil.getClan(instance.clanID);
	}

	public List<Objective> getObjectives() {
		return instance.objectives;
	}

	public List<Objective> getCompletedObjectives() {
		return instance.objectives.stream().filter(Objective::isCompleted).collect(Collectors.toList());
	}

	public Objective getObjective(int level) {
		return instance.objectives.stream().filter(o -> o.getLevel() == level).findFirst().orElse(null);
	}

	public int getNextObjective() {
		int next = 0;
		for (int i = 1; i < 19; i++) {
			if (!getObjective(i).isCompleted()) {
				next = getObjective(i).getLevel();
				break;
			}
		}
		return next;
	}

	public void updateObjective(Objective current, Objective replacement) {
		instance.objectives.remove(current);
		instance.objectives.add(replacement);
	}

	public Role getRole(Position role) {
		Role result = null;
		for (Role a : instance.roles) {
			if (a.getRole().equals(role)) {
				result = a;
			}
		}
		return result;
	}

	public Role getRole(int priority) {
		Role result = null;
		for (Role a : instance.roles) {
			if (a.getPriority() == priority) {
				result = a;
			}
		}
		return result;
	}

	public void addRole(Role role) {
		instance.roles.add(role);
	}

	/**
	 * The function to call whenever finished making changes to a village.
	 * This ensures updates to instance references get applied.
	 */
	public void complete() {
		try {
			if (getMeta() != null) {
				ClanMeta meta = getMeta();
				PersistentClan.deleteInstance(meta.getId());
			}
		} catch (NullPointerException ignored) {
		}
		DataManager dm = new DataManager("Villages", "Configuration");
		Config v = dm.getFile(ConfigType.MISC_FILE);
		v.getConfig().set(instance.clanID, instance.id.toString());
		v.saveConfig();
		PersistentClan clan = new PersistentClan(instance.clanID);
		clan.setValue(instance, 0);
		clan.storeTemp();
		clan.saveMeta(425);
		ClansVillages.loadVillages();
	}

	public ClanMeta getMeta() {
		HUID id = instance.getOwner().getId(425);
		return PersistentClan.loadTempInstance(id);
	}

	public void sendMessage(String text) {
		instance.inhabitants.forEach(i -> {
			if (i.getUser().isOnline()) {
				i.getUser().getPlayer().sendMessage(Clan.clanUtil.color(Clan.clanUtil.getPrefix() + " " + text));
			}
		});
	}

	/** ================ [ VILLAGE MISC end ] ================= */

	/** ================ [ INVITATIONAL ] ================= */

	public void invitePlayer(UUID inviter, UUID invited) {
		Invitation invite = new Invitation(inviter, invited, instance);
		instance.invitations.add(invite);
	}

	public void unInvitePlayer(String invited) {
		for (Invitation i : instance.invitations) {
			if (Objects.equals(i.getInvited().getName(), invited)) {
				instance.invitations.remove(i);
				break;
			}
		}
	}

	public void acceptInvite(Invitation invite) {
		UUID invited = invite.getInvited().getUniqueId();
		Inhabitant i = new Inhabitant(invited, instance, HempfestClans.clanManager(Bukkit.getPlayer(invited)));
		i.giveRole(Position.VILLAGE_MEMBER);
		instance.unInvitePlayer(invite.getInvited().getName());
	}

	public void denyInvite(Invitation invite) {
		instance.unInvitePlayer(invite.getInvited().getName());
	}

	public Invitation getInvite(Player invited) {
		Invitation result = null;
		for (Invitation i : instance.invitations) {
			if (Objects.equals(i.getInvited().getName(), invited.getName())) {
				result = i;
				break;
			}
		}
		return result;
	}

	public Invitation getInvite(String invited) {
		Invitation result = null;
		for (Invitation i : instance.invitations) {
			if (i.getInvited().getName().equals(invited)) {
				result = i;
				break;
			}
		}
		return result;
	}

	public List<Invitation> getInvitations() {
		return instance.invitations;
	}

	public boolean isInvited(Player invited) {
		return instance.invitations.stream().anyMatch(i -> Objects.equals(i.getInvited().getName(), invited.getName()));
	}

	public boolean isInvited(String name) {
		return instance.invitations.stream().anyMatch(i -> Objects.equals(i.getInvited().getName(), name));
	}

	/** ================ [ INVITATIONAL end ] ================= */

	/** ================ [ ALARM ] ================= */

	public void setAlarmLoc(Location loc) {
		instance.alarmLoc = loc;
	}

	public Location getAlarm() {
		return instance.alarmLoc;
	}

	public void usedAlarm(boolean b) {
		this.usedAlarm = b;
	}

	public boolean usedAlarm() {
		return usedAlarm;
	}

	/** ================ [ ALARM end ] ================= */

	/** ================ [ OUTPOST ] ================= */

	public void setOutpostLoc(Location loc) {
		instance.outpostLoc = loc;
	}

	public Location getOutpost() {
		return instance.outpostLoc;
	}

	/** ================ [ OUTPOST end ] ================= */

	/** ================ [ HALL ] ================= */

	public void setHallLoc(Location loc) {
		instance.hallLoc = loc;
	}

	public Location getHall() {
		return instance.hallLoc;
	}

	/** ================ [ HALL end ] ================= */

	/** ================ [ INHABITANT ] ================= */

	public void addInhabitant(Inhabitant inhabitant) {
		instance.inhabitants.add(inhabitant);
	}

	public void removeInhabitant(Inhabitant inhabitant) {
		instance.inhabitants.remove(inhabitant);
	}

	public boolean isInhabitant(Player p) {
		return instance.inhabitants.stream().anyMatch(i -> Objects.equals(i.getUser().getName(), p.getName()));
	}

	public boolean isInhabitant(String name) {
		return instance.inhabitants.stream().anyMatch(i -> Objects.equals(i.getUser().getName(), name));
	}

	public Inhabitant getInhabitant(String name) {
		return instance.inhabitants.stream().filter(i -> Objects.equals(i.getUser().getName(), name)).findFirst().orElse(null);
	}

	public int getPopulation() {
		return instance.inhabitants.size();
	}

	public List<Inhabitant> getInhabitants() {
		return instance.inhabitants;
	}

	/** ================ [ INHABITANT end ] ================= */

	/** ================ [ BUFFS ] ================= */

	public void addBuff(PotionBuff buff) {
		instance.buffs.add(buff);
	}

	public void updateBuff(PotionBuff current, PotionBuff replacement) {
		instance.buffs.remove(current);
		instance.buffs.add(replacement);
	}

	public void removeBuff(PotionEffectType type) {
		for (PotionBuff b : instance.buffs) {
			if (b.getEffect().getType().equals(type)) {
				buffs.remove(b);
			}
		}
	}

	public void removeBuff(PotionBuff buff) {
		instance.buffs.remove(buff);
	}

	public boolean hasBuff(PotionEffectType type) {
		return instance.buffs.stream().anyMatch(b -> b.getEffect().getType().equals(type));
	}

	public List<PotionBuff> getBuffs() { return instance.buffs; }

	/** ================ [ BUFFS end ] ================= */


}

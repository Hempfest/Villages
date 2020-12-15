package com.youtube.hempfest.villages.command;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.formatting.component.Text;
import com.youtube.hempfest.hempcore.formatting.component.Text_R2;
import com.youtube.hempfest.hempcore.formatting.string.ColoredString;
import com.youtube.hempfest.hempcore.formatting.string.PaginatedAssortment;
import com.youtube.hempfest.hempcore.gui.GuiLibrary;
import com.youtube.hempfest.hempcore.library.Message;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.activities.PotionBuff;
import com.youtube.hempfest.villages.apicore.entities.Inhabitant;
import com.youtube.hempfest.villages.apicore.entities.Role;
import com.youtube.hempfest.villages.apicore.entities.Village;
import com.youtube.hempfest.villages.apicore.gui.InventoryObjectives;
import com.youtube.hempfest.villages.apicore.library.Buff;
import com.youtube.hempfest.villages.apicore.library.Permission;
import com.youtube.hempfest.villages.apicore.library.Position;
import com.youtube.hempfest.villages.apicore.permissive.BuffFinder;
import com.youtube.hempfest.villages.apicore.permissive.PermFinder;
import com.youtube.hempfest.villages.apicore.permissive.RoleFinder;
import com.youtube.hempfest.villages.events.VillageDeletionEvent;
import com.youtube.hempfest.villages.events.VillageInhabitantJoinEvent;
import com.youtube.hempfest.villages.events.VillageInhabitantLeaveEvent;
import com.youtube.hempfest.villages.events.VillageInvitationEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CommandVillage extends BukkitCommand {

	public CommandVillage() {
		super("village");
		setAliases(Collections.singletonList("v"));
	}

	protected List<String> color(String... text) {
		ArrayList<String> convert = new ArrayList<>();
		for (String t : text)
			convert.add((new ColoredString(t, ColoredString.ColorType.MC)).toString());
		return convert;
	}

	public ItemStack makePersistentItem(Material material, String displayName, String key, String data, String... lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();
		assert itemMeta != null;
		itemMeta.setDisplayName((new ColoredString(displayName, ColoredString.ColorType.MC)).toString());
		itemMeta.getPersistentDataContainer().set(new NamespacedKey(ClansVillages.getInstance(), key), PersistentDataType.STRING, data);
		itemMeta.setLore(color(lore));
		item.setItemMeta(itemMeta);
		return item;
	}

	public List<String> helpMenu() {
		List<String> help = new ArrayList<>();
		help.add("&7|&b) &3/village &7create");
		help.add("&7|&b) &3/village &cleave");
		help.add("&7|&b) &3/village &binfo");
		help.add("&7|&b) &3/village &7who &b<&7inhabitant&b>");
		help.add("&7|&b) &3/village &7invite &b<&7playerName&b>");
		help.add("&7|&b) &3/village &7permit &b<&7inhabitant/roleName&b> <&7permission&b>");
		help.add("&7|&b) &3/village &7take &b<&7inhabitant/roleName&b> <&7permission&b>");
		help.add("&7|&b) &3/village &4disband");
		help.add("&7|&b) &3/village &7addbuff &b<&7type&b>");
		help.add("&7|&b) &3/village &7rembuff &b<&7type&b>");
		help.add("&7|&b) &3/village &ebuffs");
		help.add("&7|&b) &3/village &eobjectives");
		help.add("&7|&b) &3/village &7give &b<&7inhabitant&b> <&7role&b>");
		help.add("&7|&b) &3/village &7remove &b<&7inhabitant&b> <&7role&b>");
		help.add("&7|&b) &3/village &7objective &b<&7level&b/&7clear&b>");
		help.add("&7|&b) &3/village &ehall");
		help.add("&7|&b) &3/village &eoutpost");
		help.add("&7|&b) &3/village &b&osetoutpost");
		help.add("&7|&b) &3/village &b&osethall");
		help.add("&7|&b) &3/village &7accept &b<&7clanName&b>");
		help.add("&7|&b) &3/village &7deny &b<&7clanName&b>");
		help.add("&7|&b) &3/village &apay");
		help.add("&7|&b) &3/village rent &b<&7amount&b>");
		help.add("&7|&b) &3/village tax &b<&7amount&b>");
		help.add("&7|&b) &3/village bank &b<&7bal&b,&7deposit&b,&7withdraw&7&b>");
		help.add("&7|&b) &3/village &bsetmotd");
		help.add("&7|&b) &3/village &amotd");
		return help;
	}

	private final List<String> arguments = new ArrayList<String>();

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			arguments.clear();
			arguments.addAll(Arrays.asList("create", "motd", "setmotd", "bank", "leave", "info", "who", "invite", "accept", "deny", "rent", "tax", "pay", "permit", "take", "disband", "addbuff", "rembuff", "buffs", "objectives", "objective", "give", "remove"));
			for (String b : arguments) {
				if (b.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(b);
			}
			return result;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("addbuff")) {
				arguments.clear();
				arguments.addAll(BuffFinder.getBuffs());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("bank")) {
				arguments.clear();
				arguments.addAll(Arrays.asList("balance", "deposit", "withdraw"));
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("invite")) {
				arguments.clear();
				for (Player p : Bukkit.getOnlinePlayers()) {
					arguments.add(p.getName());
				}
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("who")) {
				arguments.clear();
				for (Player p : Bukkit.getOnlinePlayers()) {
					arguments.add(p.getName());
				}
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("rembuff")) {
				arguments.clear();
				arguments.addAll(BuffFinder.getBuffs());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("permit")) {
				arguments.clear();
				for (Position pos : Position.values()) {
					arguments.add(pos.name().toLowerCase());
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					arguments.add(p.getName());
				}
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("take")) {
				arguments.clear();
				for (Position pos : Position.values()) {
					arguments.add(pos.name().toLowerCase());
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					arguments.add(p.getName());
				}
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[1].toLowerCase()))
						result.add(b);
				}
				return result;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				arguments.clear();
				arguments.addAll(RoleFinder.getRoles());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[2].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("remove")) {
				arguments.clear();
				arguments.addAll(RoleFinder.getRoles());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[2].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("permit")) {
				arguments.clear();
				arguments.addAll(PermFinder.getPermissions());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[2].toLowerCase()))
						result.add(b);
				}
				return result;
			}
			if (args[0].equalsIgnoreCase("take")) {
				arguments.clear();
				arguments.addAll(PermFinder.getPermissions());
				for (String b : arguments) {
					if (b.toLowerCase().startsWith(args[2].toLowerCase()))
						result.add(b);
				}
				return result;
			}
		}

		return null;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;
		int length = args.length;
		Message msg = new Message(p, Clan.clanUtil.getPrefix());

		if (Clan.clanUtil.getClan(p) == null) {
			Village v = null;
			for (Village village : ClansVillages.getVillages()) {
				if (village.isInhabitant(p.getName())) {
					v = village;
					break;
				}
			}
			if (v != null) {
				v.removeInhabitant(v.getInhabitant(p.getName()));
				v.complete();
			}
			msg.send("&c&oVillage features are reserved for members of clans.");
			return true;
		}

		if (length == 0) {
			PaginatedAssortment assortment = new PaginatedAssortment(p, helpMenu());
			msg = new Message(p, null);
			msg.send("&3Villages command help. &f(&7/village #page&f)");
			assortment.setListTitle("&b&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			assortment.setListBorder("&b&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
			assortment.setNavigateCommand("v");
			assortment.setLinesPerPage(10);
			assortment.export(1);
			return true;
		}


		if (length == 1) {
			if (args[0].equalsIgnoreCase("leave")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					VillageInhabitantLeaveEvent event = new VillageInhabitantLeaveEvent(v, p, msg);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						event.perform();
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("sethall")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					Inhabitant i = v.getInhabitant(p.getName());
					if (i.hasPermission(Permission.UPDATE_HALL)) {
						v.setHallLoc(p.getLocation());
						v.complete();
						v.sendMessage("&3&oVillage hall updated.");
					} else {
						// no perms
						msg.send("&c&oYou are not permitted to change the village hall location. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("setoutpost")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					Inhabitant i = v.getInhabitant(p.getName());
					if (i.hasPermission(Permission.UPDATE_OUTPOST)) {
						v.setOutpostLoc(p.getLocation());
						v.complete();
						v.sendMessage("&3&oVillage outpost updated.");
					} else {
						// no perms
						msg.send("&c&oYou are not permitted to change the village outpost location. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("outpost")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.USE_OUTPOST)) {
						msg.send("&c&oYou are not permitted to use the village outpost. Ask the chief for permission.");
						return true;
					}
					if (v.getOutpost() != null) {
						Location og = v.getOutpost();
						msg.send("&aTeleporting in 10 seconds.");
						Bukkit.getScheduler().scheduleSyncDelayedTask(ClansVillages.getInstance(), () -> p.teleport(og), 10 * 20);

					} else {
						msg.send("&cYour village doesn't currently have an outpost.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("hall")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.USE_HALL)) {
						msg.send("&c&oYou are not permitted to use the village hall. Ask the chief for permission.");
						return true;
					}
					if (v.getHall() != null) {
						Location og = v.getHall();
						msg.send("&aTeleporting in 10 seconds.");
						Bukkit.getScheduler().scheduleSyncDelayedTask(ClansVillages.getInstance(), () -> p.teleport(og), 10 * 20);

					} else {
						msg.send("&cYour village doesn't currently have a hall.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("create")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					msg.send("&c&oYou are already a member of a village.");
					return true;
				}

				Clan c = HempfestClans.clanManager(p);
				// make way to check if bell already given. Then charge for another if so.
				ItemStack bell = makePersistentItem(Material.BELL, "&6&lVILLAGE ALARM", "clan-id", c.getClanID(), "");
				if (!Arrays.asList(p.getInventory().getContents()).contains(bell)) {
					p.getInventory().addItem(bell);
					msg.send("&aPlace down the alarm to begin leveling your village.");
				} else {
					msg.send("&c&oYou already have an alarm, place it down to begin leveling your village.");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("disband")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_CHIEF)) {
						msg.send("&c&oOnly a chief can disband the village.");
						return true;
					}
					VillageDeletionEvent event = new VillageDeletionEvent(v, p);
					Bukkit.getPluginManager().callEvent(event);
					if (!event.isCancelled()) {
						event.perform();
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("motd")) {
				// give book
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.getMotdBook() != null) {
						if (!Arrays.asList(p.getInventory().getContents()).contains(v.getMotdBook())) {
							p.getInventory().addItem(v.getMotdBook());
							msg.send("&aMotd book has been given.");
						} else {
							msg.send("&c&oYou already have the book.");
							return true;
						}
					} else {
						msg.send("&c&oYour village doesn't have an motd.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("setmotd")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					Inhabitant i = v.getInhabitant(p.getName());
					if (i.hasPermission(Permission.UPDATE_MOTD)) {
						if (p.getInventory().getItemInMainHand().getType().equals(Material.WRITTEN_BOOK)) {
							v.setMotdBook(p.getInventory().getItemInMainHand());
							v.complete();
							v.sendMessage("&7&oThe village motd has been updated.");
						} else {
							msg.send("&c&oInvalid item. You need a written book.");
							return true;
						}
					} else {
						// no perm
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("objectives") || args[0].equalsIgnoreCase("missions")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}

				if (v != null) {
					GuiLibrary lib = HempCore.guiManager(p);
					lib.setData(v.getId().toString());
					new InventoryObjectives(lib).open();
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("info")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					boolean alarmSet;
					if (v.getAlarm() == null) {
						alarmSet = false;
					} else {
						alarmSet = true;
					}
					boolean hallSet;
					if (v.getHall() == null) {
						hallSet = false;
					} else {
						hallSet = true;
					}
					boolean outpostSet;
					if (v.getOutpost() == null) {
						outpostSet = false;
					} else {
						outpostSet = true;
					}
					msg.send(Clan.clanUtil.getColor(v.getOwner().getChatColor()) + v.getOwner().getClanTag() + " &f| &3&oVillage information");
					msg = new Message(p, null);
					msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					if (alarmSet) {
						msg.send("&3Alarm: " + "&ainstalled");
					} else {
						msg.send("&3Alarm: " + "&c&omissing");
					}
					if (hallSet) {
						if (Bukkit.getVersion().contains("1.16")) {
							msg.build(new Text().textRunnable("&3Hall: ", "&f(&b&lCLICK&f)", "Click to teleport.", "v hall"));
						} else {
							msg.build(Text_R2.textRunnable("&3Hall: ", "&f(&b&lCLICK&f)", "Click to teleport.", "v hall"));
						}
					} else {
						msg.send("&3Hall: " + "&c&oNot set");
					}
					if (outpostSet) {
						if (Bukkit.getVersion().contains("1.16")) {
							msg.build(new Text().textRunnable("&3Outpost: ", "&f(&b&lCLICK&f)", "Click to teleport.", "v outpost"));
						} else {
							msg.build(Text_R2.textRunnable("&3Outpost: ", "&f(&b&lCLICK&f)", "Click to teleport.", "v outpost"));
						}
					} else {
						msg.send("&3Outpost: " + "&c&oNot set");
					}
					msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					msg.send("&3Level: &f(&b" + v.getLevel() + "&f)");
					msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					msg.send("&3Village inhabitants &f(&b" + v.getInhabitants().size() + "&f)");
					msg.send("&3Complete Objectives: &f(&b" + v.getCompletedObjectives().size() + "&f/ &b" + v.getObjectives().size() + "&f)");
					int chunks = 0;
					for (Inhabitant i : v.getInhabitants()) {
						chunks += Arrays.asList(i.getClan().getOwnedClaims()).size();
					}
					msg.send("&3Owned Land: &f(&b" + chunks + "&f) chunk(s)");
					msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					if (v.getDailyPayment() > 0) {
						msg.send("&3&lRent: &6&n" + v.getDailyPayment());
						msg.send("&b&oLate Tax: &c&o" + v.getLateTax());
						msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					}
					msg.send("&3Bank: &6&o" + v.getVillageBankBalance());
					msg.send("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					msg.send("&7&odictated by &3&l&o" + v.getOwner().getClanTag());
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("bank")) {
				// not enough args {balance, deposit, withdraw}
				return true;
			}
			if (args[0].equalsIgnoreCase("pay")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.getInhabitant(p.getName()).hasPermission(Permission.PAY_TO_STAY) && !v.getInhabitant(p.getName()).hasPermission(Permission.ALL)) {
						double balance = ClansVillages.getEconomy().getBalance(p);
						double owed = v.getDailyPayment();
						if (v.getInhabitant(p.getName()).getTaxOwed() > 0) {
							double taxI = v.getInhabitant(p.getName()).getTaxOwed();
							if (taxI < 1.00) {
								taxI = 1.13;
							}
							owed = owed * taxI;
						}
						if (owed > balance) {
							// not enough
							double needed = owed - balance;
							msg.send("&c&oYou don't have enough money. Amount needed: &6&o" + needed);
						} else {
							if (!v.getInhabitant(p.getName()).hasPayed()) {
								v.getInhabitant(p.getName()).setHasPayed(true);
								ClansVillages.getEconomy().withdrawPlayer(p, owed);
								v.giveMoney(v.getDailyPayment());
								v.getInhabitant(p.getName()).addTax(-owed);
								v.complete();
								msg.send("&a&oYour rent has been payed. You are free to stay for +1 day that you are online.");

							} else {
								msg.send("&c&oYou have already paid your dues.");
								return true;
							}
						}
					} else {
						msg.send("&c&oYou are not required to pay rent here. You may live free.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("buffs")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					Clan.clanUtil.sendMessage(p, "All village buffs. &f(&b" + v.getBuffs().size() + "&f)");
					for (PotionBuff b : v.getBuffs()) {
						p.sendMessage(" - " + b.getEffect().getType().getName());
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("permit")) {
				msg.send("&c&oInvalid usage: &7/village permit &f<&6playerName&f,&6roleName&f> <&6&oaction&f>");
				return true;
			}
			if (args[0].equalsIgnoreCase("bank")) {
				msg.send("&c&oInvalid usage: &7/village bank &f<&6bal&f,&6deposit&f,&6withdraw&f> <&6&oamount?&f>");
				return true;
			}
			try {
				int page = Integer.parseInt(args[0]);
				PaginatedAssortment assortment = new PaginatedAssortment(p, helpMenu());
				msg = new Message(p, null);
				msg.send("&3Villages command help. &f(&7/village #page&f)");
				assortment.setListTitle("&b&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				assortment.setListBorder("&b&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
				assortment.setNavigateCommand("v");
				assortment.setLinesPerPage(10);
				assortment.export(page);
			} catch (NumberFormatException e) {
				msg.send("&c&oInvalid page number / sub-command");
			}
			return true;
		}

		if (length == 2) {
			if (args[0].equalsIgnoreCase("bank")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (args[1].equalsIgnoreCase("balance") || args[1].equalsIgnoreCase("bal")) {
					if (v != null) {
						msg.send("Bank balance: &6&l" + v.getVillageBankBalance());
					} else {

						return true;
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("deposit")) {
					msg.send("&c&oInvalid usage: &7/v bank deposit &camount");
					return true;
				}
				if (args[1].equalsIgnoreCase("withdraw")) {
					msg.send("&c&oInvalid usage: &7/v bank withdraw &camount");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("rent")) {
				try {
					double amount = Double.parseDouble(args[1]);
					Village v = null;
					for (Village village : ClansVillages.getVillages()) {
						if (village.isInhabitant(p.getName())) {
							v = village;
							break;
						}
					}
					if (v != null) {
						if (v.getInhabitant(p.getName()).hasPermission(Permission.ADJUST_RENT)) {
							v.setDailyPayment(amount);
							v.complete();
							v.sendMessage("&3&o" + p.getName() + " &7&ohas adjusted the village rent charge to: &6&l" + amount);
						} else {
							msg.send("&c&oYou are not permitted to adjust the village rent charge.");
							return true;
						}
					} else {
						msg.send("&c&oYou are not apart of a village..");
						return true;
					}
				} catch (NumberFormatException e) {
					msg.send("&c&oInvalid amount. Expected format ##.##");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("tax")) {
				try {
					double amount = Double.parseDouble(args[1]);
					Village v = null;
					for (Village village : ClansVillages.getVillages()) {
						if (village.isInhabitant(p.getName())) {
							v = village;
							break;
						}
					}
					if (v != null) {
						if (v.getInhabitant(p.getName()).hasPermission(Permission.ADJUST_RENT_TAX)) {
							v.setLateTax(amount);
							v.complete();
							v.sendMessage("&3&o" + p.getName() + " &7&ohas adjusted the village late tax to: &6&l" + amount);
						} else {
							msg.send("&c&oYou are not permitted to adjust the village rent charge.");
							return true;
						}
					} else {
						msg.send("&c&oYou are not apart of a village..");
						return true;
					}
				} catch (NumberFormatException e) {
					msg.send("&c&oInvalid amount. Expected format ##.##");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("objective")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.LEVEL_OBJECTIVE)) {
						msg.send("&c&oYou are not permitted to complete objectives. Ask the chief for permission.");
						return true;
					}
					if (args[1].equalsIgnoreCase("clear")) {
						v.getInhabitant(p.getName()).setObjective(0);
						v.complete();
						msg.send("Objective focus now cleared. No longer leveling.");
						return true;
					}
					try {
						int level = Integer.parseInt(args[1]);
						if (level > v.getObjectives().size() || level == 0) {
							msg.send("&cAn objective level this high doesn't exist!");
							return true;
						}
						if (v.getObjective(level).isCompleted()) {
							msg.send("&c&oThis objective is already done!");
							return true;
						}
						if (level == 11 && !v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_PRIEST)) {
							msg.send("&c&oYou are not permitted to level this objective. This requires role &6&oPriest");
							return true;
						}
						if (level == 12 && !v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_WARRIOR)) {
							msg.send("&c&oYou are not permitted to level this objective. This requires role &4&oWarrior");
							return true;
						}
						if (level != v.getNextObjective()) {
							msg.send("&c&oYou are too low level! You must compelete your next objective. (&2&l" + v.getNextObjective() + "&c&o)");
							return true;
						}
						v.getInhabitant(p.getName()).setObjective(level);
						v.complete();
						msg.send("Now targeting objective level &b" + level);
						msg.send(v.getObjective(level).info());
					} catch (NumberFormatException e) {
						msg.send("&c&oInvalid objective level!");
					}

				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}

				return true;
			}
			if (args[0].equalsIgnoreCase("who")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.isInhabitant(args[1])) {
						Inhabitant i = v.getInhabitant(args[1]);
						// show players stats
						Clan.clanUtil.sendMessage(p, "&b&l" + i.getUser().getName() + " &3&oinformation");
						List<String> perms = new ArrayList<>();
						for (Permission perm : i.getPermissions()) {
							perms.add(perm.name().toLowerCase());
						}
						List<String> roles = new ArrayList<>();
						for (Position pos : i.getRoles()) {
							roles.add(pos.name().toLowerCase());
						}
						p.sendMessage(" ");
						p.sendMessage(Clan.clanUtil.color("&3&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
						p.sendMessage(Clan.clanUtil.color("&7&lRoles: &f&o" + roles.toString()));
						p.sendMessage(Clan.clanUtil.color("&7&lPermissions: &f&o" + perms.toString()));
						p.sendMessage(" ");
						p.sendMessage(Clan.clanUtil.color("&7&lObjectives:"));
						p.sendMessage(Clan.clanUtil.color(" - &7&lCompleted: &f(&a&l" + i.getCompletedObjectives() + "&f)"));
						if (i.getCurrentObjective() != 0) {
							p.sendMessage(Clan.clanUtil.color(" - &7&lCurrent: &f&o" + v.getObjective(i.getCurrentObjective()).info()));
						} else {
							p.sendMessage(Clan.clanUtil.color(" - &7&lCurrent: &f(&7None&f)"));
						}
						if (i.getUser().isOnline()) {
							p.sendMessage(Clan.clanUtil.color("&7&lStatus: &aOnline"));
						} else {
							p.sendMessage(Clan.clanUtil.color("&7&lStatus: &cOffline"));
						}
						p.sendMessage(Clan.clanUtil.color("&7&lAlarms Destroyed: &f(&c" + i.getAlarmsDestroyed() + "&f)"));
						p.sendMessage(Clan.clanUtil.color("&3&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
						p.sendMessage(" ");
					} else {
						msg.send("&c&oVillage inhabitant not found..");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("invite")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}

				if (v != null) {
					if (args[1].equalsIgnoreCase(p.getName())) {
						msg.send("&c&oYou cannot invite yourself to your own village.");
						return true;
					}
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null) {
						if (Clan.clanUtil.getClan(target) == null) {
							msg.send("&c&oThe targeted player is not a member of a clan.. They aren't trust worthy.");
							return true;
						}
						Clan t = HempfestClans.clanManager(target);
						if (!Clan.clanUtil.getAllies(v.getOwner().getClanID()).contains(t.getClanID()) && !t.getClanID().equals(v.getOwner().getClanID())) {
							msg.send("&c&oThe targeted player has no close relation to us.. They aren't trust worthy.");
							return true;
						}
						Inhabitant i = v.getInhabitant(p.getName());
						if (!i.hasPermission(Permission.INVITE_PLAYER)) {
							msg.send("&c&oYou are not permitted to invite people to the village. Ask the chief for permission.");
							return true;
						}
						VillageInvitationEvent event = new VillageInvitationEvent(v, p, target, msg);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled()) {
							event.perform();
						}
					} else {
						msg.send("&c&oThe target was not found..");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("accept")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					msg.send("&c&oYou are already a member of a village.");
					return true;
				}
				String id = Clan.clanUtil.getClanID(args[1]);
				if (id != null) {
					Clan clan = Clan.clanUtil.getClan(id);
					if (clan.getId(425) != null) {
						v = ClansVillages.getVillageByMetaId(clan.getId(425));
						if (!v.isInvited(p)) {
							msg.send("&c&oYou were not invited stay at our village..");
							return true;
						}
						VillageInhabitantJoinEvent event = new VillageInhabitantJoinEvent(v, p);
						if (!event.isCancelled()) {
							event.perform();
						}
					}
				} else {
					msg.send("&c&oThe village you're trying to enter doesn't exist.");
				}
			}

			if (args[0].equalsIgnoreCase("deny")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					msg.send("&c&oYou are already a member of a village.");
					return true;
				}
				String id = Clan.clanUtil.getClanID(args[1]);
				if (id != null) {
					Clan clan = Clan.clanUtil.getClan(id);
					if (clan.getId(425) != null) {
						v = ClansVillages.getVillageByMetaId(clan.getId(425));
						if (!v.isInvited(p)) {
							msg.send("&c&oYou were not invited stay at our village..");
							return true;
						}
						v.denyInvite(v.getInvite(p));
						v.complete();
						for (Inhabitant i : v.getInhabitants()) {
							if (i.getUser().isOnline()) {
								i.getUser().getPlayer().sendMessage(Clan.clanUtil.color(Clan.clanUtil.getPrefix() + " &a&oPlayer " + p.getName() + " has denied our invitation to the village."));
							}
						}
					}
				} else {
					msg.send("&c&oThe village you're trying to deny doesn't exist.");
				}
			}
			if (args[0].equalsIgnoreCase("addbuff")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.getInhabitant(p.getName()).hasPermission(Permission.ADD_BUFF)) {
						Buff b = BuffFinder.getBuff(args[1]);
						if (b != null) {
							if (!v.hasBuff(b.getEffect().getType())) {
								if (v.getBuffs().size() == 1 && v.getLevel() <= 5) {
									msg.send("&cYour village isn't strong enough. Level more to gain more buffs!");
									return true;
								}
								if (v.getBuffs().size() == 2 && v.getLevel() == 10) {
									msg.send("&cYour village isn't strong enough. Level more to gain more buffs!");
									return true;
								}
								if (v.getBuffs().size() == 3) {
									msg.send("&cYour village has reached the max amount of buffs!");
									return true;
								}
								new PotionBuff(b.getEffect(), v);
								v.complete();
								v.sendMessage("&a&l" + p.getName() + " &3added a new buff to owned land.");
							} else {
								// buff already added
								msg.send("&cThis buff is already applied.");
								return true;
							}
						} else {
							// unfound buff
							msg.send("&cBuff unknown...");
							return true;
						}
					} else {
						// no permission
						msg.send("&c&oYou are not permitted to add buffs to the village. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("rembuff")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (v.getInhabitant(p.getName()).hasPermission(Permission.REMOVE_BUFF)) {
						Buff b = BuffFinder.getBuff(args[1]);
						if (b != null) {
							if (v.hasBuff(b.getEffect().getType())) {
								v.removeBuff(b.getEffect().getType());
								v.complete();
								v.sendMessage("&a&l" + p.getName() + " &3removed a buff from owned land.");
							} else {
								// buff not added
								msg.send("&cThis buff isn't currently applied.");
								return true;
							}
						} else {
							// unfound buff
							msg.send("&cBuff unknown...");
							return true;
						}
					} else {
						// no permission
						msg.send("&c&oYou are not permitted to remove buffs from the village. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("kick")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.KICK_INHABITANT)) {
						msg.send("&c&oYou are not permitted to kick inhabitants. Ask the chief for permission.");
						return true;
					}
					if (!v.isInhabitant(args[1])) {
						msg.send("&cInhabitant was not found...");
						return true;
					}
					Inhabitant i = v.getInhabitant(args[1]);
					if (i.getPrimaryRole().getPriority() >= v.getInhabitant(p.getName()).getPrimaryRole().getPriority()) {
						msg.send("&c&oYou cannot kick this user. They have greater than or equal to power.");
						return true;
					}
					v.removeInhabitant(i);
					v.complete();
					v.sendMessage("&c&l" + args[1] + " &7&owas kicked from inhabitancy.");
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("take")) {
				msg.send("&c&oInvalid usage: &7/village take &f<&6playerName&f,&6roleName&f> <&6&oaction&f>");
				return true;
			}
			if (args[0].equalsIgnoreCase("permit")) {
				msg.send("&c&oInvalid usage: &7/village permit &f<&6playerName&f,&6roleName&f> <&6&oaction&f>");
				return true;
			}
			if (args[0].equalsIgnoreCase("message")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					String result = args[1];
					if (v.getInhabitant(p.getName()).hasPermission(Permission.BROADCAST_MESSAGE)) {
						v.sendMessage("&6[&3&lVILLAGE&6] " + result);
					} else {
						msg.send("&c&oYou are not permitted to broadcast messages to the village. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			return true;
		}

		if (length == 3) {
			if (args[0].equalsIgnoreCase("bank")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (args[1].equalsIgnoreCase("balance") || args[1].equalsIgnoreCase("bal")) {
					// look for clan village args[2]
					Village v2 = null;
					for (Village village : ClansVillages.getVillages()) {
						if (village.getOwner().getClanTag().equalsIgnoreCase(args[2])) {
							v2 = village;
							break;
						}
					}
					if (v2 != null) {
						msg.send("Village bank balance: &6&l" + v2.getVillageBankBalance());
					} else {
						msg.send("&c&oThere was no village found ruled under: &f&o&n" + args[2]);
						return true;
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("deposit")) {
					if (v != null) {
						Inhabitant i = v.getInhabitant(p.getName());
						if (i.hasPermission(Permission.BANK_DEPOSIT)) {
							try {
								double amount = Double.parseDouble(args[2]);
								double balance = ClansVillages.getEconomy().getBalance(p);
								if (amount > balance) {
									msg.send("&c&oYou do not have enough money.");
								} else {
									v.giveMoney(amount);
									ClansVillages.getEconomy().withdrawPlayer(p, amount);
									v.complete();
									v.sendMessage("&c&o" + p.getName() + " &7&ohas put &6&n" + amount + " &7&ointo the village bank.");
									return true;
								}
							} catch (NumberFormatException e) {
								msg.send("&c&oWrong format, expected ##.##");
								return true;
							}
						} else {
							msg.send("&c&oYou are not permitted to deposit into the village bank. Ask the chief for permission.");
							return true;
						}
					} else {
						msg.send("&c&oYou are not apart of a village..");
						return true;
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("withdraw")) {
					if (v != null) {
						Inhabitant i = v.getInhabitant(p.getName());
						if (i.hasPermission(Permission.BANK_WITHDRAW)) {
							try {
								double amount = Double.parseDouble(args[2]);
								double balance = v.getVillageBankBalance();
								if (amount > balance) {
									msg.send("&c&oYour village isn't rich enough.");
								} else {
									v.takeMoney(amount);
									ClansVillages.getEconomy().depositPlayer(p, amount);
									v.complete();
									v.sendMessage("&c&o" + p.getName() + " &7&ohas taken &6&n" + amount + " &7&ofrom the village bank.");
									return true;
								}
							} catch (NumberFormatException e) {
								msg.send("&c&oWrong format, expected ##.##");
								return true;
							}
						} else {
							msg.send("&c&oYou are not permitted to withdraw from the village bank. Ask the chief for permission.");
							return true;
						}
					} else {
						msg.send("&c&oYou are not apart of a village..");
						return true;
					}
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("give")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.MANAGE_PERMISSIONS)) {
						msg.send("&c&oYou are not permitted to manage village permissions. Ask the chief for permission.");
						return true;
					}
					Position pos = RoleFinder.getRole(args[2]);
					if (pos == null) {
						msg.send("&c&oInvalid action, valid types: " + RoleFinder.getRoles().toString());
						return true;
					}
					if (!v.isInhabitant(args[1])) {
						msg.send("&cInhabitant was not found...");
						return true;
					}
					Inhabitant i = v.getInhabitant(args[1]);
					if (i.getPrimaryRole().getPriority() >= v.getInhabitant(p.getName()).getPrimaryRole().getPriority() && !v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_CHIEF)) {
						msg.send("&c&oYou cannot modify this users permission. They have greater than or equal to power.");
						return true;
					}
					if (!i.hasRole(pos)) {
						if (pos == Position.VILLAGE_WARDEN) {
							if (!v.getObjective(9).isCompleted()) {
								Objective o = v.getObjective(9);
								if (v.getInhabitant(p.getName()).getCurrentObjective() == 9) {
									if (o.completionPercentage() < 100.00) {
										o.addProgress(1);
										v.complete();
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b9&f) &b&l" + o.completionPercentage() + "&f% done.")));
									}
									if (o.completionPercentage() == 100.00) {
										o.setCompleted(true);
										v.getInhabitant(p.getName()).completed(9);
										v.getInhabitant(p.getName()).setObjective(0);
										v.complete();
										for (Inhabitant in : v.getInhabitants()) {
											if (in.getUser().isOnline()) {
												in.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
												in.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
											}
										}
									}
								}
							}
						}
						if (pos == Position.VILLAGE_PRIEST) {
							if (!v.getObjective(13).isCompleted()) {
								Objective o = v.getObjective(13);
								if (v.getInhabitant(p.getName()).getCurrentObjective() == 13) {
									if (o.completionPercentage() < 100.00) {
										o.addProgress(1);
										v.complete();
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b13&f) &b&l" + o.completionPercentage() + "&f% done.")));
									}
									if (o.completionPercentage() == 100.00) {
										o.setCompleted(true);
										v.getInhabitant(p.getName()).completed(13);
										v.getInhabitant(p.getName()).setObjective(0);
										v.complete();
										for (Inhabitant in : v.getInhabitants()) {
											if (in.getUser().isOnline()) {
												in.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
												in.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
											}
										}
									}
								}
							}
						}
						if (pos == Position.VILLAGE_WARRIOR) {
							if (!v.getObjective(14).isCompleted()) {
								Objective o = v.getObjective(14);
								if (v.getInhabitant(p.getName()).getCurrentObjective() == 14) {
									if (o.completionPercentage() < 100.00) {
										o.addProgress(1);
										v.complete();
										p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&e&lObjective &f(&b14&f) &b&l" + o.completionPercentage() + "&f% done.")));
									}
									if (o.completionPercentage() == 100.00) {
										o.setCompleted(true);
										v.getInhabitant(p.getName()).completed(14);
										v.getInhabitant(p.getName()).setObjective(0);
										v.complete();
										for (Inhabitant in : v.getInhabitants()) {
											if (in.getUser().isOnline()) {
												in.getUser().getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Clan.clanUtil.color("&3&lVillage &alevel up. &f(&a" + v.getLevel() + "&f)")));
												in.getUser().getPlayer().playSound(i.getUser().getPlayer().getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
											}
										}
									}
								}
							}
						}
						i.giveRole(pos);
						v.complete();
						msg.send("&6&oGave role &f" + pos.name().toLowerCase() + " &6&oto inhabitant &f" + args[1]);
					} else {
						msg.send("&cInhabitant already inherits from this role.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("remove")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.MANAGE_PERMISSIONS)) {
						msg.send("&c&oYou are not permitted to manage village permissions. Ask the chief for permission.");
						return true;
					}
					Position pos = RoleFinder.getRole(args[2]);
					if (pos == null) {
						msg.send("&c&oInvalid action, valid types: " + RoleFinder.getRoles().toString());
						return true;
					}
					if (!v.isInhabitant(args[1])) {
						msg.send("&cInhabitant was not found...");
						return true;
					}
					Inhabitant i = v.getInhabitant(args[1]);
					if (i.getPrimaryRole().getPriority() >= v.getInhabitant(p.getName()).getPrimaryRole().getPriority() && !v.getInhabitant(p.getName()).hasRole(Position.VILLAGE_CHIEF)) {
						msg.send("&c&oYou cannot modify this users permission. They have greater than or equal to power.");
						return true;
					}
					if (i.hasRole(pos)) {
						i.takeRole(pos);
						v.complete();
						msg.send("&6&oTook role &f" + pos.name().toLowerCase() + " &6&ofrom inhabitant &f" + args[1]);
					} else {
						msg.send("&cInhabitant does not currently inherit from this role.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("permit")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.MANAGE_PERMISSIONS)) {
						msg.send("&c&oYou are not permitted to manage permissions for the village. Ask the chief for permission.");
						return true;
					}
					if (!v.isInhabitant(args[1])) {
						for (Position r : Position.values()) {
							if (r.name().toLowerCase().replace("_", "").equalsIgnoreCase(args[1].replace("_", ""))) {
								Role role = v.getRole(r);
								Permission permission = PermFinder.getPermission(args[2]);
								if (permission == null) {
									msg.send("&c&oInvalid action, valid types: " + PermFinder.getPermissions().toString());
									return true;
								}
								if (!role.hasPermission(permission)) {
									role.addPermission(permission);
									v.complete();
									msg.send("&6&oGave permission &7" + args[2] + " &6&oto role &7" + r.name());
								} else {
									msg.send("&c&oThe role already has direct access to this permission!");
									return true;
								}
								return true;
							}
						}
						msg.send("&c&oUnable to permit. This user isn't an inhabitant of the village.");
						return true;
					}
					Inhabitant i = v.getInhabitant(args[1]);
					Permission permission = PermFinder.getPermission(args[2]);
					if (permission == null) {
						msg.send("&c&oInvalid action, valid types: " + PermFinder.getPermissions().toString());
						return true;
					}
					if (!i.hasPermission(permission)) {
						i.givePermission(permission);
						v.complete();
						msg.send("&6&oGave permission &7" + args[2] + " &6&oto player &7" + i.getUser().getName());
					} else {
						msg.send("&c&oThe inhabitant already has direct access to this permission!");
						return true;
					}
				} else {
					// Not in a village
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("take")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					if (!v.getInhabitant(p.getName()).hasPermission(Permission.MANAGE_PERMISSIONS)) {
						msg.send("&c&oYou are not permitted to manage permissions for the village. Ask the chief for permission.");
						return true;
					}
					if (!v.isInhabitant(args[1])) {
						for (Position r : Position.values()) {
							if (r.name().toLowerCase().replace("_", "").equalsIgnoreCase(args[1].replace("_", ""))) {
								Role role = v.getRole(r);
								Permission permission = PermFinder.getPermission(args[2]);
								if (permission == null) {
									msg.send("&c&oInvalid action, valid types: " + PermFinder.getPermissions().toString());
									return true;
								}
								if (role.hasPermission(permission)) {
									role.removePermission(permission);
									v.complete();
									msg.send("&6&oTook permission &7" + args[2] + " &e&ofrom role &7" + r.name().toLowerCase());
								} else {
									msg.send("&cThe role doesn't have direct access to this permission!");
									return true;
								}
								return true;
							}
						}
						msg.send("&c&oUnable to permit. This user isn't an inhabitant of the village.");
						return true;
					}
					Inhabitant i = v.getInhabitant(args[1]);
					if (i.getPrimaryRole().getPriority() >= v.getInhabitant(p.getName()).getPrimaryRole().getPriority()) {
						msg.send("&c&oYou cannot modify this users permission. They have greater than or equal to power.");
						return true;
					}
					Permission permission = PermFinder.getPermission(args[2]);
					if (permission == null) {
						msg.send("&c&oInvalid action, valid types: " + PermFinder.getPermissions().toString());
						return true;
					}
					if (i.hasPermission(permission)) {
						i.takePermission(permission);
						v.complete();
						msg.send("&e&oTook permission &7" + args[2] + " &e&ofrom player &7" + i.getUser().getName());
					} else {
						msg.send("&cThe inhabitant doesn't have direct access to this permission!");
						return true;
					}
				} else {
					// not in a village
					msg.send("&c&oYou are not apart of a village..");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("message")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.isInhabitant(p.getName())) {
						v = village;
						break;
					}
				}
				if (v != null) {
					String result = args[1] + " " + args[2];
					if (v.getInhabitant(p.getName()).hasPermission(Permission.BROADCAST_MESSAGE)) {
						v.sendMessage("&6[&3&lVILLAGE&6] " + result);
					} else {
						msg.send("&c&oYou are not permitted to broadcast messages to the village. Ask the chief for permission.");
						return true;
					}
				} else {
					msg.send("&c&oYou are not apart of a village..");
					return true;
				}
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("message")) {
			Village v = null;
			for (Village village : ClansVillages.getVillages()) {
				if (village.isInhabitant(p.getName())) {
					v = village;
					break;
				}
			}
			if (v != null) {
				StringBuilder rsn = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					rsn.append(args[i]).append(" ");
				}
				String result = rsn.substring(0, rsn.length() - 1);
				if (v.getInhabitant(p.getName()).hasPermission(Permission.BROADCAST_MESSAGE)) {
					v.sendMessage("&6[&3&lVILLAGE&6] " + result);
				} else {
					msg.send("&c&oYou are not permitted to broadcast messages to the village. Ask the chief for permission.");
					return true;
				}
			} else {
				msg.send("&c&oYou are not apart of a village..");
				return true;
			}
			return true;
		}
		return true;
	}
}

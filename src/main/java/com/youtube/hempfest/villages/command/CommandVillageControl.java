package com.youtube.hempfest.villages.command;

import com.github.sanctum.labyrinth.formatting.string.PaginatedAssortment;
import com.github.sanctum.labyrinth.library.Message;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

public class CommandVillageControl extends BukkitCommand {

	public CommandVillageControl() {
		super("villagecontrol");
		setAliases(Collections.singletonList("vc"));
		setPermission("villages.admin");
	}


	private final List<String> arguments = new ArrayList<String>();

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			arguments.clear();
			arguments.addAll(Arrays.asList("bank", "give", "take"));
			for (String b : arguments) {
				if (b.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(b);
			}
			return result;
		}
		return null;
	}

	public List<String> helpMenu() {
		List<String> help = new ArrayList<>();
		help.add("&7|&b) &3/village &7bank &b<&7clanName&b>");
		help.add("&7|&b) &3/village &agive &b<&7clanName&b> &b<&7amount&b>");
		help.add("&7|&b) &3/village &ctake &b<&7clanName&b> &b<&7amount&b>");
		return help;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player p = (Player) sender;
		int length = args.length;
		Message msg = new Message(p, Clan.clanUtil.getPrefix() + " [&3&lADMIN&7]");
		if (!p.hasPermission(this.getPermission())) {
			msg.send("&4&oYou do not have permission to do this.");
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
			if (args[0].equalsIgnoreCase("bank")) {
				msg.send("&c&oInvalid usage: &7/vc bank &4clanName");
				return true;
			}
			if (args[0].equalsIgnoreCase("give")) {
				msg.send("&c&oInvalid usage: &7/vc give &4clanName &7amount");
				return true;
			}
			if (args[0].equalsIgnoreCase("take")) {
				msg.send("&c&oInvalid usage: &7/vc take &4clanName &7amount");
				return true;
			}
			return true;
		}

		if (length == 2) {
			if (args[0].equalsIgnoreCase("bank")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.getOwner().getClanTag().equalsIgnoreCase(args[1])) {
						v = village;
						break;
					}
				}
				if (v != null) {
					msg.send("[" + args[1] + "] (&6Bank&7) : &6&o" + v.getVillageBankBalance());
				} else {
					msg.send("&c&oThere was no village found ruled under: &f&o&n" + args[1]);
					return true;
				}
				return true;
			}
			return true;
		}

		if (length == 3) {
			if (args[0].equalsIgnoreCase("give")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.getOwner().getClanTag().equalsIgnoreCase(args[1])) {
						v = village;
						break;
					}
				}
				if (v != null) {
					try {
						double amount = Double.parseDouble(args[2]);
						v.giveMoney(amount);
						v.complete();
						v.sendMessage("&d&oStaff member &e" + p.getName() + " &d&o granted us: &6&l" + amount);
					} catch (NumberFormatException e) {
						// wrong format
						msg.send("&c&oWrong format, expected ##.##");
						return true;
					}
				} else {
					msg.send("&c&oThere was no village found ruled under: &f&o&n" + args[1]);
					return true;
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("take")) {
				Village v = null;
				for (Village village : ClansVillages.getVillages()) {
					if (village.getOwner().getClanTag().equalsIgnoreCase(args[1])) {
						v = village;
						break;
					}
				}
				if (v != null) {
					try {
						double amount = Double.parseDouble(args[2]);
						v.takeMoney(amount);
						v.complete();
						v.sendMessage("&4&oStaff member &e" + p.getName() + " &4&otook from us: &c-&6&l" + amount);
					} catch (NumberFormatException e) {
						// wrong format
						msg.send("&c&oWrong format, expected ##.##");
						return true;
					}
				} else {
					msg.send("&c&oThere was no village found ruled under: &f&o&n" + args[1]);
					return true;
				}
				return true;
			}
			return true;
		}

		return true;
	}
}

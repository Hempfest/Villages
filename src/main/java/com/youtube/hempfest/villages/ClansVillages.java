package com.youtube.hempfest.villages;

import com.youtube.hempfest.clans.metadata.ClanMeta;
import com.youtube.hempfest.clans.metadata.PersistentClan;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.data.ConfigType;
import com.youtube.hempfest.clans.util.data.DataManager;
import com.youtube.hempfest.hempcore.command.CommandBuilder;
import com.youtube.hempfest.hempcore.event.EventBuilder;
import com.youtube.hempfest.hempcore.library.HFEncoded;
import com.youtube.hempfest.hempcore.library.HUID;
import com.youtube.hempfest.villages.apicore.entities.Item;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClansVillages extends JavaPlugin {

	private static ClansVillages instance;

	private static Economy econ = null;

	private static final List<Village> villages = new ArrayList<>();

	private static final List<Location> alarms = new ArrayList<>();

	@Override
	public void onEnable() {
		// Plugin startup logic
		instance = this;
		startUp = true;
		loadVillages();
		startUp = false;
		new EventBuilder(this).compileFields("com.youtube.hempfest.villages.listener");
		new CommandBuilder(this).compileFields("com.youtube.hempfest.villages.command");
		if (!economyFound()) {
			getLogger().severe("- Disabled due to no vault economy found.");
			getServer().getPluginManager().disablePlugin(this);
		}
		makeItems();
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}

	private void makeItems() {
		/** ------------ \/ PRIEST \/--------------*/
		Item scepter = new Item(Material.BLAZE_ROD, "&f&lSCEPTER OF DIVINE FAITH");
		scepter.setKey("scepter_of_divine_faith");
		scepter.makeItem();
		scepter.addEnchant(Enchantment.DAMAGE_ALL, 10);
		scepter.addEnchant(Enchantment.DAMAGE_UNDEAD, 5);
		scepter.addEnchant(Enchantment.VANISHING_CURSE, 1);
		scepter.setItem('M', new ItemStack(Material.STICK));
		scepter.setItem('G', new ItemStack(Material.GOLD_INGOT));
		scepter.setItem('T', Item.bladeItem());
		scepter.recipeShape("TGT", "GMG", "TGT");
		scepter.register();
		/** ------------ \/ TRIDENT \/--------------*/
		Item trident = new Item(Material.TRIDENT, "&3&lTRUSTY VILLAGE TRIDENT");
		trident.setKey("trusty_village_trident");
		trident.makeItem();
		trident.addEnchant(Enchantment.VANISHING_CURSE, 1);
		trident.setItem('M', new ItemStack(Material.TRIDENT));
		trident.setItem('O', new ItemStack(Material.AIR));
		trident.setItem('G', Item.bladeItem());
		trident.recipeShape("OGO", "GMG", "OGO");
		trident.register();
		/** ------------ \/ WARRIOR \/--------------*/
		Item greatSword = new Item(Material.DIAMOND_SWORD, "&b&lWARRIOR'S BLADE OF JUDGEMENT");
		greatSword.setKey("warriors_blade_of_judgement");
		greatSword.makeItem();
		greatSword.addEnchant(Enchantment.KNOCKBACK, 6);
		greatSword.addEnchant(Enchantment.SWEEPING_EDGE, 4);
		greatSword.addEnchant(Enchantment.VANISHING_CURSE, 1);
		greatSword.setItem('O', Item.bladeItem());
		greatSword.setItem('D', new ItemStack(Material.DIAMOND));
		greatSword.setItem('T', new ItemStack(Material.STICK));
		greatSword.recipeShape("ODO", "ODO", "OTO");
		greatSword.register();
		/** ------------ \/ ALARM \/--------------*/
		Item alarm = new Item(Material.BELL, "&6&lVILLAGE ALARM");
		alarm.setKey("village_alarm");
		alarm.makeItem();
		alarm.addEnchant(Enchantment.VANISHING_CURSE, 1);
		alarm.setItem('D', Material.DIAMOND);
		alarm.setItem('B', Material.BELL);
		alarm.setItem('O', Material.AIR);
		alarm.setItem('E', Material.ENCHANTED_BOOK);
		alarm.setItem('T', Material.REDSTONE_TORCH);
		alarm.recipeShape("DDD", "TBT", "OEO");
		alarm.register();
		/** ------------ \/ TEAR OF GOD \/--------------*/
		Item tear = new Item(Material.GHAST_TEAR, "&f&lTEAR OF GOD");
		tear.setKey("tear_of_god");
		tear.makeItem();
		tear.addEnchant(Enchantment.VANISHING_CURSE, 1);
		tear.setItem('C', Material.PRISMARINE_CRYSTALS);
		tear.setItem('S', Material.SCUTE);
		tear.setItem('O', Material.AIR);
		tear.setItem('E', Material.ENCHANTED_BOOK);
		tear.recipeShape("OSO", "SCS", "OEO");
		tear.register();
	}

	private boolean economyFound() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		getLogger().info(": [Villages] - Economy provider found. Now using: " + rsp.getProvider().getName());
		return true;
	}

	/**
	 * @return Gets an instance of economy to manage player funds with.
	 */
	public static Economy getEconomy() {
		return econ;
	}

	/**
	 * @param alarm An alarm location to query from.
	 * @return A village object containing all its values from location object.
	 */
	public static Village getVillageByAlarm(Location alarm) {
		Village village = null;
		for (Village v : villages) {
			if (v.getAlarm().equals(alarm)) {
				village = v;
				break;
			}
		}
		return village;
	}

	/**
	 * Get a villages unique hempfest id
	 *
	 * @param c The target clan to retrieve the village id from
	 * @return A hempfest unique id for the given clans village
	 */
	public static HUID getVillageId(Clan c) {
		DataManager dm = new DataManager("Villages", "Configuration");
		Config v = dm.getFile(ConfigType.MISC_FILE);
		return HUID.fromString(v.getConfig().getString(c.getClanID()));
	}

	/**
	 * Get a village object by its unique hempfest id
	 *
	 * @param villageId A hempfest unique id assigned to the village
	 * @return A village object
	 */
	public static Village getVillageById(HUID villageId) {
		return villages.stream().filter(v -> v.getId().equals(villageId)).findFirst().orElse(null);
	}

	/**
	 * Get a village object by its persistent meta id
	 *
	 * @param instanceId A persistent meta instance id from a clan object
	 * @return A village object
	 */
	public static Village getVillageByMetaId(HUID instanceId) {
		Village result = null;
		try {
			ClanMeta data = PersistentClan.loadTempInstance(instanceId);
			result = (Village) new HFEncoded(data.value(0)).deserialized();
		} catch (IOException | ClassNotFoundException e) {
			Bukkit.getLogger().severe("[Clans] - Village instance could not be retrieved.");
		}
		return result;
	}

	/**
	 * Delete a village by its persistent meta id
	 *
	 * @param instanceId A persistent meta instance id from a clan object
	 */
	public static void deleteVillageByMetaId(HUID instanceId) {
		try {
			ClanMeta data = PersistentClan.loadTempInstance(instanceId);
			DataManager dm = new DataManager("Villages", "Configuration");
			Config v = dm.getFile(ConfigType.MISC_FILE);
			v.getConfig().set(data.getClan().getClanID(), null);
			v.saveConfig();
			Bukkit.getLogger().info("[Clans] - Village instance #" + data.getId().toString() + " successfully deleted.");
			PersistentClan.deleteInstance(data.getId());
			loadVillages();
		} catch (Exception e) {
			Bukkit.getLogger().severe("[Clans] - Village instance could not be retrieved.");
		}
	}

	private boolean startUp;

	public static void loadVillages() {
		if (!villages.isEmpty()) {
			villages.clear();
		}
		Config c = new Config(null, "Clans");
		for (File f : c.getDataFolder().listFiles()) {
			String name = f.getName().replace(".yml", "");
			Config clan = new Config(name, "Clans");
			if (clan.getConfig().isConfigurationSection("Data")) {
				for (String s : clan.getConfig().getConfigurationSection("Data").getKeys(false)) {
					if (clan.getConfig().getInt("Data." + s) == 425) {
						HUID id = HUID.fromString(s);
						try {
							Village v = getVillageByMetaId(id);
							int i = v.getInhabitants().size();
							villages.add(v);
							if (v.getAlarm() != null) {
								alarms.add(v.getAlarm());
							}
						} catch (NullPointerException e) {
							Bukkit.getLogger().severe("[Clans] - Village instance could not be retrieved for id #" + id.toString());
						}
					}
				}
			}
		}
		if (instance.startUp) {
			int ihs = 0;
			instance.getLogger().info("- All loadable instances have been refreshed. Found (" + getVillages().size() + ") total villages.");
			for (Village v : getVillages()) {
				ihs += v.getInhabitants().size();
			}
			instance.getLogger().info("- A total number of (" + ihs + ") village inhabitants have been accounted for.");
		}
	}

	/**
	 * @return A list of all known villages within the server.
	 */
	public static List<Village> getVillages() {
		return villages;
	}

	public static ClansVillages getInstance() {
		return instance;
	}

	public List<Location> getAllAlarms() {
		return alarms;
	}


}

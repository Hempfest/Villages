package com.youtube.hempfest.villages.apicore.gui;

import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.hempcore.HempCore;
import com.youtube.hempfest.hempcore.gui.GuiLibrary;
import com.youtube.hempfest.hempcore.gui.Menu;
import com.youtube.hempfest.hempcore.gui.Pagination;
import com.youtube.hempfest.hempcore.library.HUID;
import com.youtube.hempfest.villages.ClansVillages;
import com.youtube.hempfest.villages.apicore.activities.Objective;
import com.youtube.hempfest.villages.apicore.entities.Village;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class InventoryObjectives extends Pagination {

	public InventoryObjectives(GuiLibrary guiLibrary) {
		super(guiLibrary);
	}

	private final String villageId = guiLibrary.getData();

	@Override
	public String getMenuName() {
		return Clan.clanUtil.color("&3&oVillage objectives &8»");
	}

	@Override
	public int getSlots() {
		return 54;
	}

	@Override
	public void handleMenu(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Village v = ClansVillages.getVillageById(HUID.fromString(villageId));
		String id = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(HempCore.getInstance(), "objective-id"), PersistentDataType.STRING);
		switch (e.getCurrentItem().getType()) {
			case EMERALD:
				p.closeInventory();
				Bukkit.dispatchCommand(p, "v objective " + id);
				break;
			case BARRIER:
				p.closeInventory();
				break;
			case DARK_OAK_BUTTON:
				if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")) {
					if (page == 0) {
						p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
					} else {
						page = page - 1;
						super.open();
					}
				} else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())
						.equalsIgnoreCase("Right")) {
					if (!((index + 1) >= v.getObjectives().size())) {
						page = page + 1;
						super.open();
					} else {
						p.sendMessage(ChatColor.GRAY + "You are on the last page.");
					}
				}
				break;
		}
	}

	@Override
	public void setMenuItems() {
		Village v = ClansVillages.getVillageById(HUID.fromString(villageId));
		ItemStack item;
		addMenuBorder();
		List<Objective> objectives = new ArrayList<>(v.getObjectives());
		if (objectives != null && !objectives.isEmpty()) {
			for (int i = 0; i < getMaxItemsPerPage(); i++) {
				index = getMaxItemsPerPage() * page + i;
				if (index >= objectives.size())
					break;
				if (objectives.get(index) != null) {
					if (objectives.get(index).isCompleted()) {
						item = makePersistentItem(Material.DIAMOND, "&3&lObjective (&b" + objectives.get(index).getLevel() + "&3&l)", "objective-id", String.valueOf(objectives.get(index).getLevel()), "&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&lName&2: &f" + objectives.get(index).getName(), "&2Description: &f" + objectives.get(index).info(), "&a&oCompletion: &f" + objectives.get(index).completionPercentage() + "%", "&a&oDone: &f" + objectives.get(index).isCompleted());
					} else {
						item = makePersistentItem(Material.EMERALD, "&3&lObjective (&b" + objectives.get(index).getLevel() + "&3&l)", "objective-id", String.valueOf(objectives.get(index).getLevel()), "&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&lName&2: &f" + objectives.get(index).getName(), "&2Description: &f" + objectives.get(index).info(), "&a&oCompletion: &f" + objectives.get(index).completionPercentage() + "%", "&a&oDone: &f" + objectives.get(index).isCompleted());
					}
					inventory.addItem(item);
				}
			}
		}
		setFillerGlassLight();
	}
}

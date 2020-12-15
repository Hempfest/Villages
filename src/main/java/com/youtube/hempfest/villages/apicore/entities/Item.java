package com.youtube.hempfest.villages.apicore.entities;

import com.google.common.collect.MapMaker;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.villages.ClansVillages;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class Item {

	private final ConcurrentMap<Character, Material> recipeMap = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	private final ConcurrentMap<Character, ItemStack> recipeStackMap = new MapMaker().
			weakKeys().
			weakValues().
			makeMap();

	private final Material mat;

	private final String name;

	private NamespacedKey key;

	private ShapedRecipe recipe;

	private ItemStack item;

	public Item(Material appearance, String name) {
		this.mat = appearance;
		this.name = name;
	}

	public void setKey(String key) {
		this.key = new NamespacedKey(ClansVillages.getInstance(), key);
	}

	public void setItem(Character key, Material item) {
		recipeMap.put(key, item);
	}

	public void setItem(Character key, ItemStack item) {
		recipeStackMap.put(key, item);
	}

	public void makeItem() {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Clan.clanUtil.color(name));
		item.setItemMeta(meta);
		this.item = item;
	}

	public void addEnchant(Enchantment e, int level) {
		ItemStack i = item;
		i.addUnsafeEnchantment(e, level);
		this.item = i;
	}

	@SuppressWarnings("deprecation")
	public void recipeShape(String... shape) {
		ShapedRecipe recipe = new ShapedRecipe(key, item);
		List<String> list = Arrays.asList(shape);
		recipe.shape(list.get(0), list.get(1), list.get(2));
		if (!this.recipeStackMap.isEmpty()) {
			recipe.setIngredient((list.get(0)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(0))));
			recipe.setIngredient((list.get(0)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(1))));
			recipe.setIngredient((list.get(0)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(0).charAt(2))));
			recipe.setIngredient((list.get(1)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(0))));
			recipe.setIngredient((list.get(1)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(1))));
			recipe.setIngredient((list.get(1)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(1).charAt(2))));
			recipe.setIngredient((list.get(2)).charAt(0), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(0))));
			recipe.setIngredient((list.get(2)).charAt(1), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(1))));
			recipe.setIngredient((list.get(2)).charAt(2), new RecipeChoice.ExactChoice(this.recipeStackMap.get(list.get(2).charAt(2))));
		} else {
			recipe.setIngredient((list.get(0)).charAt(0), this.recipeMap.get(list.get(0).charAt(0)));
			recipe.setIngredient((list.get(0)).charAt(1), this.recipeMap.get(list.get(0).charAt(1)));
			recipe.setIngredient((list.get(0)).charAt(2), this.recipeMap.get(list.get(0).charAt(2)));
			recipe.setIngredient((list.get(1)).charAt(0), this.recipeMap.get(list.get(1).charAt(0)));
			recipe.setIngredient((list.get(1)).charAt(1), this.recipeMap.get(list.get(1).charAt(1)));
			recipe.setIngredient((list.get(1)).charAt(2), this.recipeMap.get(list.get(1).charAt(2)));
			recipe.setIngredient((list.get(2)).charAt(0), this.recipeMap.get(list.get(2).charAt(0)));
			recipe.setIngredient((list.get(2)).charAt(1), this.recipeMap.get(list.get(2).charAt(1)));
			recipe.setIngredient((list.get(2)).charAt(2), this.recipeMap.get(list.get(2).charAt(2)));
		}
		this.recipe = recipe;
	}

	public void register() {
		Bukkit.addRecipe(recipe);
	}

	public static ItemStack alarmItem() {
		ItemStack item = new ItemStack(Material.BELL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Clan.clanUtil.color("&6&lVILLAGE ALARM"));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		return item;
	}

	public static ItemStack bladeItem() {
		ItemStack item = new ItemStack(Material.GHAST_TEAR);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Clan.clanUtil.color("&f&lTEAR OF GOD"));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
		return item;
	}


}

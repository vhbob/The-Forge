package com.vhbob.tf.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.Main;

public class General {

	Plugin pl;

	FileConfiguration configData = Main.getPlugin(Main.class).getCustomConfig();
	File pluginData = new File(Main.getPlugin(Main.class).getDataFolder(), "pluginData.yml");

	List<String> rarities = Arrays.asList("Common", "Uncommon", "Rare", "Epic", "Legendary");

	public General(Plugin plugin) {
		pl = plugin;
	}

	public ItemStack customItem(String name, ArrayList<String> lore, Material type, int amt) {
		ItemStack item = new ItemStack(type);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.BOLD + name);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		item.setAmount(amt);
		return item;
	}

	public BlockFace getBlockFace(Player player) {
		String prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix"))
				+ ChatColor.RESET;
		List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks(null, 100);
		if (lastTwoTargetBlocks.size() != 2 || !lastTwoTargetBlocks.get(1).getType().isOccluding()) {
			player.sendMessage(prefix + ChatColor.RED + "Please target a block that completely covers the space.");
			player.sendMessage(ChatColor.RED + "Ex: A block of dirt is fine but a flower is not");
			return null;
		}
		Block targetBlock = lastTwoTargetBlocks.get(1);
		Block adjacentBlock = lastTwoTargetBlocks.get(0);
		return targetBlock.getFace(adjacentBlock);
	}

	public List<String> getRarities() {
		return rarities;
	}

	public ItemStack commonDust() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack dust = customItem(ChatColor.WHITE + "" + ChatColor.BOLD + "Common Dust", lore, Material.SULPHUR, 1);
		return dust;
	}

	public ItemStack uncommonDust() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack dust = customItem(ChatColor.RED + "" + ChatColor.BOLD + "Uncommon Dust", lore, Material.SUGAR, 1);
		return dust;
	}

	public ItemStack rareDust() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack dust = customItem(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Dust", lore, Material.GLOWSTONE_DUST,
				1);
		return dust;
	}

	public ItemStack epicDust() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack dust = customItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Epic Dust", lore, Material.REDSTONE,
				1);
		return dust;
	}

	public ItemStack legendaryDust() {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack dust = customItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Legendary Dust", lore,
				Material.BLAZE_POWDER, 1);
		return dust;
	}

	public void saveCustomConfig() {
		try {
			configData.save(pluginData);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public ItemStack stripCostLore(ItemStack item) {
		ItemStack newItem = item.clone();
		ItemMeta itemMeta = newItem.getItemMeta();
		if (newItem != null && !newItem.getType().equals(Material.AIR)) {
			if (newItem.hasItemMeta()) {
				if (newItem.getItemMeta().hasLore()) {
					ArrayList<String> oldLore = (ArrayList<String>) newItem.getItemMeta().getLore();
					ArrayList<String> lore = (ArrayList<String>) oldLore.clone();
					for (String string : oldLore) {
						if (ChatColor.stripColor(string).toLowerCase().contains("dust")) {
							lore.remove(string);
						}
					}
					itemMeta.setLore(lore);
				}
			}
		}
		newItem.setItemMeta(itemMeta);
		return newItem;
	}

	public void saveLocation(double x, double y, double z, String world, float pitch, float yaw, String locName) {
		configData.set(locName + ".X", x);
		configData.set(locName + ".Y", y);
		configData.set(locName + ".Z", z);
		configData.set(locName + ".World", world);
		configData.set(locName + ".Pitch", pitch);
		configData.set(locName + ".Yaw", yaw);
		try {
			configData.save(pluginData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDustCost(String rarity, int index, int common, int uncommon, int rare, int epic, int legendary) {
		configData.set("Costs." + rarity + "." + index + ".Common", common);
		configData.set("Costs." + rarity + "." + index + ".Uncommon", uncommon);
		configData.set("Costs." + rarity + "." + index + ".Rare", rare);
		configData.set("Costs." + rarity + "." + index + ".Epic", epic);
		configData.set("Costs." + rarity + "." + index + ".Legendary", legendary);
		try {
			configData.save(pluginData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean hasAvaliableSlot(Inventory inv) {
		for (int i = 0; i < 36; i++) {
			if (inv.getItem(i) == null) {
				return true;
			}
		}
		return false;
	}

}

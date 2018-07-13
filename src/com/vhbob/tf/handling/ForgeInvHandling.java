package com.vhbob.tf.handling;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.Main;
import com.vhbob.tf.utils.General;

public class ForgeInvHandling implements Listener {

	FileConfiguration configData = Main.getPlugin(Main.class).getCustomConfig();
	File pluginData = new File(Main.getPlugin(Main.class).getDataFolder(), "pluginData.yml");

	Plugin pl;
	String prefix;

	General utils;

	public ForgeInvHandling(Plugin plugin) {
		pl = plugin;
		prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix")) + ChatColor.RESET;
		utils = new General(pl);
	}

	@EventHandler
	public void interactInv(InventoryClickEvent e) {
		if (e.getInventory().getName()
				.equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("Name")))) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
					&& e.getCurrentItem().getItemMeta().getDisplayName() != null) {
				String itemName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).toLowerCase();
				if (itemName.contains("common")) {
					Inventory next = Bukkit.createInventory(null, 45, ChatColor.WHITE + "" + ChatColor.BOLD + "Common");
					addItems(next, "Common");
					e.getWhoClicked().openInventory(next);
				}
				if (itemName.contains("uncommon")) {
					Inventory next = Bukkit.createInventory(null, 45, ChatColor.RED + "" + ChatColor.BOLD + "Uncommon");
					addItems(next, "Uncommon");
					e.getWhoClicked().openInventory(next);
				}
				if (itemName.contains("rare")) {
					Inventory next = Bukkit.createInventory(null, 45, ChatColor.BLUE + "" + ChatColor.BOLD + "Rare");
					addItems(next, "Rare");
					e.getWhoClicked().openInventory(next);
				}
				if (itemName.contains("epic")) {
					Inventory next = Bukkit.createInventory(null, 45,
							ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Epic");
					addItems(next, "Epic");
					e.getWhoClicked().openInventory(next);
				}
				if (itemName.contains("legendary")) {
					Inventory next = Bukkit.createInventory(null, 45,
							ChatColor.GOLD + "" + ChatColor.BOLD + "Legendary");
					addItems(next, "Legendary");
					e.getWhoClicked().openInventory(next);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void addItems(Inventory inv, String rarity) {
		if (configData.contains("Items." + rarity)) {
			ArrayList<ItemStack> items = (ArrayList<ItemStack>) configData.getList("Items." + rarity);
			for (int i = 0; i < items.size(); i++) {
				ItemStack it = items.get(i);
				ItemStack item = it.clone();
				List<String> lore;
				if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
					lore = item.getItemMeta().getLore();
				} else {
					lore = new ArrayList<>();
				}
				int commonCost = configData.getInt("Costs." + rarity + "." + i + ".Common");
				int uncommonCost = configData.getInt("Costs." + rarity + "." + i + ".Uncommon");
				int rareCost = configData.getInt("Costs." + rarity + "." + i + ".Rare");
				int epicCost = configData.getInt("Costs." + rarity + "." + i + ".Epic");
				int legendaryCost = configData.getInt("Costs." + rarity + "." + i + ".Legendary");
				ItemMeta itemMeta = item.getItemMeta();
				lore.add(ChatColor.GRAY + "" + ChatColor.BOLD + "-=Dust Costs=-");
				if (commonCost != 0) {
					lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Common Dust: " + ChatColor.RESET + commonCost);
				}
				if (uncommonCost != 0) {
					lore.add(ChatColor.RED + "" + ChatColor.BOLD + "Uncommon Dust: " + ChatColor.RESET + uncommonCost);
				}
				if (rareCost != 0) {
					lore.add(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Dust: " + ChatColor.RESET + rareCost);
				}
				if (epicCost != 0) {
					lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Epic Dust: " + ChatColor.RESET + epicCost);
				}
				if (legendaryCost != 0) {
					lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Legendary Dust: " + ChatColor.RESET
							+ legendaryCost);
				}
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				inv.setItem(i, item);
				;
			}
		}
	}

}

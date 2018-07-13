package com.vhbob.tf.handling;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.Main;
import com.vhbob.tf.utils.General;

public class ForgingHandling implements Listener {

	FileConfiguration configData = Main.getPlugin(Main.class).getCustomConfig();
	File pluginData = new File(Main.getPlugin(Main.class).getDataFolder(), "pluginData.yml");

	Plugin pl;
	String prefix;

	General utils;

	ItemStack commonDust;
	ItemStack uncommonDust;
	ItemStack rareDust;
	ItemStack epicDust;
	ItemStack legendaryDust;

	public ForgingHandling(Plugin plugin) {
		pl = plugin;
		prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix")) + ChatColor.RESET;
		utils = new General(pl);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void clickForgableItem(InventoryClickEvent e) {
		commonDust = utils.commonDust();
		uncommonDust = utils.uncommonDust();
		rareDust = utils.rareDust();
		epicDust = utils.epicDust();
		legendaryDust = utils.legendaryDust();
		String name = ChatColor.stripColor(e.getInventory().getName());
		if (utils.getRarities().contains(name) && e.getWhoClicked() instanceof Player && e.getCurrentItem() != null) {
			Player p = (Player) e.getWhoClicked();
			if (configData.contains("Items." + name)) {
				List<ItemStack> list = (List<ItemStack>) configData.getList("Items." + name);
				if (e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLore()) {
					ItemStack stripped = utils.stripCostLore(e.getCurrentItem());
					if (list.contains(stripped)) {

						int commonCost = configData.getInt("Costs." + name + "." + e.getSlot() + ".Common");
						int uncommonCost = configData.getInt("Costs." + name + "." + e.getSlot() + ".Uncommon");
						int rareCost = configData.getInt("Costs." + name + "." + e.getSlot() + ".Rare");
						int epicCost = configData.getInt("Costs." + name + "." + e.getSlot() + ".Epic");
						int legendaryCost = configData.getInt("Costs." + name + "." + e.getSlot() + ".Legendary");

						if (canForge(p, commonCost, uncommonCost, rareCost, epicCost, legendaryCost)) {
							if (utils.hasAvaliableSlot(p.getInventory())) {
								for (ItemStack item : p.getInventory()) {
									if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
										int stackAmt = item.getAmount();
										if (item.getItemMeta().getDisplayName()
												.equalsIgnoreCase(commonDust.getItemMeta().getDisplayName())) {
											while (commonCost > 0 && stackAmt > 0) {
												stackAmt--;
												commonCost--;
											}
										} else if (item.getItemMeta().getDisplayName()
												.equalsIgnoreCase(uncommonDust.getItemMeta().getDisplayName())) {
											while (uncommonCost > 0 && stackAmt > 0) {
												stackAmt--;
												uncommonCost--;
											}
										} else if (item.getItemMeta().getDisplayName()
												.equalsIgnoreCase(rareDust.getItemMeta().getDisplayName())) {
											while (rareCost > 0 && stackAmt > 0) {
												stackAmt--;
												rareCost--;
											}
										} else if (item.getItemMeta().getDisplayName()
												.equalsIgnoreCase(epicDust.getItemMeta().getDisplayName())) {
											while (epicCost > 0 && stackAmt > 0) {
												stackAmt--;
												epicCost--;
											}
										} else if (item.getItemMeta().getDisplayName()
												.equalsIgnoreCase(legendaryDust.getItemMeta().getDisplayName())) {
											while (legendaryCost > 0 && stackAmt > 0) {
												stackAmt--;
												legendaryCost--;
											}
										}
										item.setAmount(stackAmt);
									}
								}
								p.getInventory().addItem(stripped);
								p.sendMessage(prefix + ChatColor.GREEN + "You have forged a "
										+ e.getClickedInventory().getName() + ChatColor.GREEN + " item!");
								if (pl.getConfig().getBoolean("Sound")) {
									p.getLocation().getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
									p.getLocation().getWorld().playSound(p.getLocation(),
											Sound.ENTITY_FIREWORK_LARGE_BLAST_FAR, 1, 1);
								}
							} else {
								p.sendMessage(prefix + ChatColor.RED + "You do not have any inventory space!");
							}
						} else {
							p.sendMessage(prefix + ChatColor.RED + "You do not have enough dust!");
						}
						p.closeInventory();
					}
				}
			}
			e.setCancelled(true);
		}
	}

	public boolean canForge(Player p, int common, int uncommon, int rare, int epic, int legendary) {
		if (hasDust(common, commonDust, p) && hasDust(uncommon, uncommonDust, p) && hasDust(rare, rareDust, p)
				&& hasDust(epic, epicDust, p) && hasDust(legendary, legendaryDust, p)) {
			return true;
		} else {
			return false;
		}

	}

	public boolean hasDust(int amount, ItemStack dustType, Player p) {
		int counter = 0;
		for (ItemStack item : p.getInventory()) {
			if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
				if (item.getItemMeta().getDisplayName().equalsIgnoreCase(dustType.getItemMeta().getDisplayName())) {
					counter += item.getAmount();
				}
			}
		}
		if (counter >= amount) {
			return true;
		} else {
			return false;
		}
	}

}

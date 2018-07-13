package com.vhbob.tf.handling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.utils.General;

public class NoCrafting implements Listener {

	General utils;

	public NoCrafting(Plugin pl) {
		utils = new General(pl);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");
		ItemStack common = utils.customItem(ChatColor.WHITE + "" + ChatColor.BOLD + "Common Dust", lore,
				Material.SULPHUR, 1);
		ItemStack uncommon = utils.customItem(ChatColor.RED + "" + ChatColor.BOLD + "Uncommon Dust", lore,
				Material.SUGAR, 1);
		ItemStack rare = utils.customItem(ChatColor.BLUE + "" + ChatColor.BOLD + "Rare Dust", lore,
				Material.GLOWSTONE_DUST, 1);
		ItemStack epic = utils.customItem(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Epic Dust", lore,
				Material.REDSTONE, 1);
		ItemStack legendary = utils.customItem(ChatColor.GOLD + "" + ChatColor.BOLD + "Legendary Dust", lore,
				Material.BLAZE_POWDER, 1);

		List<ItemStack> customItems = Arrays.asList(common, uncommon, rare, epic, legendary);
		
		for (ItemStack i : e.getClickedInventory()) {
			if (customItems.contains(i)) {
				e.setCancelled(true);
				System.out.println("test");
			}
		}
	}

}

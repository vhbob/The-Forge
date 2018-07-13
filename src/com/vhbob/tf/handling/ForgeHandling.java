package com.vhbob.tf.handling;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.Main;
import com.vhbob.tf.utils.General;

public class ForgeHandling implements Listener {

	FileConfiguration configData = Main.getPlugin(Main.class).getCustomConfig();
	File pluginData = new File(Main.getPlugin(Main.class).getDataFolder(), "pluginData.yml");

	Plugin pl;
	String prefix;

	General utils;

	public ForgeHandling(Plugin plugin) {
		pl = plugin;
		utils = new General(pl);
	}

	@EventHandler
	public void clickForge(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (configData.contains("Forge.Loc.World")) {
				Location loc = new Location(pl.getServer().getWorld(configData.getString("Forge.Loc.World")),
						configData.getDouble("Forge.Loc.X"), configData.getDouble("Forge.Loc.Y"),
						configData.getDouble("Forge.Loc.Z"));
				Block b = loc.getWorld().getBlockAt(loc);
				if (e.getClickedBlock().equals(b)) {
					e.setCancelled(true);

					ArrayList<String> lore = new ArrayList<>();

					ItemStack common = utils.customItem(ChatColor.GRAY + "" + "Forge a " + ChatColor.WHITE
							+ ChatColor.BOLD + "Common " + ChatColor.GRAY + "item", lore, Material.STAINED_GLASS_PANE,
							1);
					common.setDurability((short) 0);
					ItemStack uncommon = utils.customItem(ChatColor.GRAY + "Forge an " + ChatColor.RED + ""
							+ ChatColor.BOLD + "Uncommon " + ChatColor.GRAY + "item", lore, Material.STAINED_GLASS_PANE,
							1);
					uncommon.setDurability((short) 14);
					ItemStack rare = utils.customItem(ChatColor.GRAY + "Forge a " + ChatColor.BLUE + "" + ChatColor.BOLD
							+ "Rare " + ChatColor.GRAY + "item", lore, Material.STAINED_GLASS_PANE, 1);
					rare.setDurability((short) 11);
					ItemStack epic = utils.customItem(ChatColor.GRAY + "Forge an " + ChatColor.DARK_PURPLE + ""
							+ ChatColor.BOLD + "Epic " + ChatColor.GRAY + "item", lore, Material.STAINED_GLASS_PANE, 1);
					epic.setDurability((short) 10);
					ItemStack legendary = utils.customItem(ChatColor.GRAY + "Forge a " + ChatColor.GOLD + ""
							+ ChatColor.BOLD + "Legendary " + ChatColor.GRAY + "item", lore,
							Material.STAINED_GLASS_PANE, 1);
					legendary.setDurability((short) 1);
					lore.add(ChatColor.GRAY + "Welcome to the forge");
					lore.add(ChatColor.GRAY + "Here you can use your dust to forge");
					lore.add(ChatColor.GRAY + "different tiers of items!");
					ItemStack forgeDesc = utils.customItem(
							ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("Name")), lore,
							Material.ANVIL, 1);

					Inventory i = pl.getServer().createInventory(null, 45,
							ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("Name")));
					i.setItem(18, common);
					i.setItem(20, uncommon);
					i.setItem(22, rare);
					i.setItem(24, epic);
					i.setItem(26, legendary);
					i.setItem(4, forgeDesc);

					fillInv(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7));

					e.getPlayer().openInventory(i);
				}
			}
		}
	}

	@EventHandler
	public void noFall(BlockPhysicsEvent e) {
		if (configData.contains("Forge.Loc.X")) {
			Location loc = new Location(pl.getServer().getWorld(configData.getString("Forge.Loc.World")),
					configData.getDouble("Forge.Loc.X"), configData.getDouble("Forge.Loc.Y"),
					configData.getDouble("Forge.Loc.Z"));
			if (e.getBlock().getLocation().equals(loc)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void breakForge(BlockBreakEvent e) {
		prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix")) + ChatColor.RESET;
		if (configData.contains("Forge.Loc.X")) {
			Location loc = new Location(pl.getServer().getWorld(configData.getString("Forge.Loc.World")),
					configData.getDouble("Forge.Loc.X"), configData.getDouble("Forge.Loc.Y"),
					configData.getDouble("Forge.Loc.Z"));
			if (e.getBlock().getLocation().equals(loc)) {
				if (e.getPlayer().hasPermission("Forge.Break") || e.getPlayer().isOp()) {
					if (e.getPlayer().getInventory().getItemInMainHand() == null || !e.getPlayer().getInventory()
							.getItemInMainHand().getType().toString().contains("SWORD")) {
						configData.set("Forge", null);
						try {
							configData.save(pluginData);
						} catch (IOException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
						for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
							if (entity instanceof ArmorStand) {
								((ArmorStand) entity).setHealth(0);
							}
						}

						e.getPlayer().sendMessage(prefix + "The Forge has been " + ChatColor.RED + "destroyed!");
					}
				} else {
					e.getPlayer().sendMessage(prefix + ChatColor.RED + "Missing permission: Forge.Break");
					e.setCancelled(true);
				}
			}
		}
	}

	public void fillInv(Inventory inv, ItemStack filler) {
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) {
				inv.setItem(i, filler);
			}
		}
	}

}

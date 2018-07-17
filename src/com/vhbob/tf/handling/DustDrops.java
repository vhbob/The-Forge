package com.vhbob.tf.handling;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.utils.General;

public class DustDrops implements Listener {

	ArrayList<Location> placed = new ArrayList<>();

	Plugin pl;

	General utils;

	public DustDrops(Plugin main) {
		pl = main;
		utils = new General(pl);
	}

	@EventHandler
	public void onKill(EntityDeathEvent e) {
		int chance = new Random().nextInt(10000);
		for (String type : pl.getConfig().getConfigurationSection("Multipliers.Mobs").getKeys(false)) {
			if (e.getEntityType().toString().equalsIgnoreCase(type)) {
				double commonChance = pl.getConfig().getDouble("Chances.Drops.Kill.Common") * 100;
				double uncommonChance = pl.getConfig().getDouble("Chances.Drops.Kill.Uncommon") * 100;
				double rareChance = pl.getConfig().getDouble("Chances.Drops.Kill.Rare") * 100;
				double epicChance = pl.getConfig().getDouble("Chances.Drops.Kill.Epic") * 100;
				double legendaryChance = pl.getConfig().getDouble("Chances.Drops.Kill.Legendary") * 100;
				checkForDrop(commonChance, uncommonChance, rareChance, epicChance, legendaryChance, chance,
						pl.getConfig().getInt("Multipliers.Mobs." + type), e.getEntity().getKiller(),
						e.getEntity().getLocation(), 0);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (!placed.contains(b.getLocation()) && (e.getPlayer().getInventory().getItemInMainHand() == null
				|| !e.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("SWORD"))) {
			int chance = new Random().nextInt(10000);
			for (String type : pl.getConfig().getConfigurationSection("Multipliers.Blocks").getKeys(false)) {
				if (b.getType().toString().equalsIgnoreCase(type)) {
					double commonChance = pl.getConfig().getDouble("Chances.Drops.Mine.Common") * 100;
					double uncommonChance = pl.getConfig().getDouble("Chances.Drops.Mine.Uncommon") * 100;
					double rareChance = pl.getConfig().getDouble("Chances.Drops.Mine.Rare") * 100;
					double epicChance = pl.getConfig().getDouble("Chances.Drops.Mine.Epic") * 100;
					double legendaryChance = pl.getConfig().getDouble("Chances.Drops.Mine.Legendary") * 100;
					checkForDrop(commonChance, uncommonChance, rareChance, epicChance, legendaryChance, chance,
							pl.getConfig().getInt("Multipliers.Blocks." + type), e.getPlayer(), b.getLocation(), 1);
				}
			}
		} else {
			placed.remove(b.getLocation());
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		placed.add(e.getBlock().getLocation());
	}

	public void checkForDrop(Double commonChance, Double uncommonChance, Double rareChance, Double epicChance,
			Double legendaryChance, int roll, double multi, Player p, Location dropLoc, int type) {

		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "Use this at /forge to forge items!");

		ItemStack common;
		ItemStack uncommon;
		ItemStack rare;
		ItemStack epic;
		ItemStack legendary;

		if (type == 1) {
			common = utils.commonDust();
			uncommon = utils.uncommonDust();
			rare = utils.rareDust();
			epic = utils.epicDust();
			legendary = utils.legendaryDust();
		} else {
			common = utils.commonDust();
			uncommon = utils.uncommonDust();
			rare = utils.rareDust();
			epic = utils.epicDust();
			legendary = utils.legendaryDust();
		}
		
		if (roll <= multi * legendaryChance) {
			spawnCircleOfParticles(p, Particle.FLAME, p.getLocation(), legendary, dropLoc);
		} else if (roll <= multi * epicChance) {
			spawnCircleOfParticles(p, Particle.VILLAGER_HAPPY, p.getLocation(), epic, dropLoc);
		} else if (roll <= multi * rareChance) {
			spawnCircleOfParticles(p, Particle.FIREWORKS_SPARK, p.getLocation(), rare, dropLoc);
		} else if (roll <= multi * uncommonChance) {
			spawnCircleOfParticles(p, Particle.CRIT_MAGIC, p.getLocation(), uncommon, dropLoc);
		} else if (roll <= multi * commonChance) {
			spawnCircleOfParticles(p, Particle.BLOCK_DUST, p.getLocation(), common, dropLoc);
		}
	}

	public void spawnCircleOfParticles(Player player, Particle p, Location particleLocation, ItemStack item,
			Location dropLocation) {
		String prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix"))
				+ ChatColor.RESET;
		if (pl.getConfig().getBoolean("Particles")) {
			for (int degree = 0; degree < 360; degree += 10) {
				double radians = Math.toRadians(degree);
				double x = Math.cos(radians);
				double z = Math.sin(radians);
				particleLocation.add(x, 1, z);
				particleLocation.getWorld().spawnParticle(p, particleLocation, 1);
				particleLocation.subtract(x, 1, z);
			}
		}
		player.getWorld().dropItemNaturally(dropLocation, item);
		player.sendMessage(prefix + "You found some " + item.getItemMeta().getDisplayName() + "!");
		if (pl.getConfig().getBoolean("Sound")) {
			particleLocation.getWorld().playSound(particleLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		}
	}

}

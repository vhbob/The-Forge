package com.vhbob.tf.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import com.vhbob.tf.Main;
import com.vhbob.tf.utils.General;

public class CreateForge implements CommandExecutor {

	FileConfiguration configData = Main.getPlugin(Main.class).getCustomConfig();
	File pluginData = new File(Main.getPlugin(Main.class).getDataFolder(), "pluginData.yml");

	String prefix;

	General utils;

	Plugin pl;

	public CreateForge(Plugin plugin) {
		pl = plugin;
		utils = new General(pl);
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("prefix")) + ChatColor.RESET;
		if (cmd.getName().equalsIgnoreCase("Forge")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length == 0) {
					if (configData.contains("Forge.Warp.X")) {
						Location loc = new Location(pl.getServer().getWorld(configData.getString("Forge.Warp.World")),
								configData.getDouble("Forge.Warp.X"), configData.getDouble("Forge.Warp.Y"),
								configData.getDouble("Forge.Warp.Z"), (float) configData.getDouble("Forge.Warp.Yaw"),
								(float) configData.getDouble("Forge.Warp.Pitch"));
						p.teleport(loc);
						p.sendMessage(prefix + ChatColor.GREEN + "Teleported to The Forge!");
					} else {
						p.sendMessage(prefix + ChatColor.RED
								+ "The Forge has not been setup yet! Ask an admin to create a forge");
					}
				} else if (args[0].equalsIgnoreCase("create") && args.length == 1) {
					if (p.hasPermission("Forge.Create") || p.isOp()) {
						if (p.getTargetBlock(null, 7) != null && p.getTargetBlock(null, 7).getType() != Material.AIR) {
							BlockFace blockFace = utils.getBlockFace(p);
							if (blockFace != null) {
								Block b = p.getTargetBlock(null, 10);
								b.setType(Material.ANVIL);
								if (blockFace.equals(BlockFace.NORTH) || blockFace.equals(BlockFace.SOUTH)) {
									b.setData((byte) 1);
								} else {
									b.setData((byte) 0);
								}
								if (configData.contains("Forge.Loc.X")) {
									Location loc = new Location(
											pl.getServer().getWorld(configData.getString("Forge.Loc.World")),
											configData.getDouble("Forge.Loc.X"), configData.getDouble("Forge.Loc.Y"),
											configData.getDouble("Forge.Loc.Z"));
									loc.getWorld().getBlockAt(loc).breakNaturally(null);
									configData.set("Forge", null);
									utils.saveCustomConfig();
									for (Entity entity : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
										if (entity instanceof ArmorStand) {
											((ArmorStand) entity).setHealth(0);
										}
									}
								}
								configData.set("Forge.Loc.X", b.getLocation().getX());
								configData.set("Forge.Loc.Y", b.getLocation().getY());
								configData.set("Forge.Loc.Z", b.getLocation().getZ());
								configData.set("Forge.Loc.World", b.getLocation().getWorld().getName());
								utils.saveCustomConfig();
								Location asloc = b.getLocation().add(.5, 1, .5);
								ArmorStand as = (ArmorStand) b.getWorld().spawnEntity(asloc, EntityType.ARMOR_STAND);
								as.setSmall(true);
								as.setMarker(true);
								as.setVisible(false);
								as.setGravity(false);
								as.setCustomName(
										ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("Name")));
								as.setCustomNameVisible(true);
								p.sendMessage(prefix + ChatColor.GREEN + "Forge created!");
								utils.saveLocation(p.getLocation().getX(), p.getLocation().getY(),
										p.getLocation().getZ(), p.getLocation().getWorld().getName(),
										p.getLocation().getPitch(), p.getLocation().getYaw(), "Forge.Warp");
							}
						} else {
							p.sendMessage(prefix + ChatColor.RED + "You must be looking at a block!");
						}
					} else {
						p.sendMessage(prefix + ChatColor.RED + "Missing permission: Forge.Create");
					}
				} else if (args[0].equalsIgnoreCase("warp")) {
					if (configData.contains("Forge.Warp.X")) {
						Location loc = new Location(pl.getServer().getWorld(configData.getString("Forge.Warp.World")),
								configData.getDouble("Forge.Warp.X"), configData.getDouble("Forge.Warp.Y"),
								configData.getDouble("Forge.Warp.Z"), (float) configData.getDouble("Forge.Warp.Yaw"),
								(float) configData.getDouble("Forge.Warp.Pitch"));
						p.teleport(loc);
						p.sendMessage(prefix + ChatColor.GREEN + "Teleported to The Forge!");
					} else {
						p.sendMessage(prefix + ChatColor.RED
								+ "The Forge has not been setup yet! Ask an admin to create a forge");
					}
				} else if (args.length == 2 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("warp")) {
					if (p.hasPermission("Forge.Set.Warp")) {
						utils.saveLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),
								p.getLocation().getWorld().getName(), p.getLocation().getPitch(),
								p.getLocation().getYaw(), "Forge.Warp");
						p.sendMessage(prefix + ChatColor.GREEN + "Forge warp set!");
					} else {
						p.sendMessage(prefix + ChatColor.RED + "Missing permission: Forge.Set.Warp");
					}
				} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
					if (p.hasPermission("Forge.Add") || p.isOp()) {
						PlayerInventory playerInv = p.getInventory();
						if (playerInv.getItemInMainHand() != null
								&& !playerInv.getItemInMainHand().getType().equals(Material.AIR)) {
							if (args[1].equalsIgnoreCase("Common")) {
								addForgeItem(playerInv.getItemInMainHand(), "Common", p);
							} else if (args[1].equalsIgnoreCase("Uncommon")) {
								addForgeItem(playerInv.getItemInMainHand(), "Uncommon", p);
							} else if (args[1].equalsIgnoreCase("Rare")) {
								addForgeItem(playerInv.getItemInMainHand(), "Rare", p);
							} else if (args[1].equalsIgnoreCase("Epic")) {
								addForgeItem(playerInv.getItemInMainHand(), "Epic", p);
							} else if (args[1].equalsIgnoreCase("Legendary")) {
								addForgeItem(playerInv.getItemInMainHand(), "Legendary", p);
							} else {
								p.sendMessage(prefix + ChatColor.RED + "Usage: /Forge Add (Rarity)");
								p.sendMessage(
										ChatColor.RED + "Proper rarity args: Common Uncommon Rare Epic Legendary");
							}
						} else {
							p.sendMessage(
									prefix + ChatColor.RED + "You must have an item in your hand to add to the forge!");
						}
					} else {
						p.sendMessage(prefix + ChatColor.RED + "Missing permission: Forge.Add");
					}
				} else if (args.length == 9 && args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("cost")) {
					if (isInt(args[3]) && isInt(args[4]) && isInt(args[5]) && isInt(args[6]) && isInt(args[7])
							&& isInt(args[8])) {
						if (utils.getRarities().contains(args[2])) {
							if (configData.contains("Items." + args[2])) {
								List<ItemStack> list = (List<ItemStack>) configData.getList("Items." + args[2]);
								int itemIndex = Integer.parseInt(args[3]) - 1;
								if (itemIndex < list.size()) {
									int commonCost = Integer.parseInt(args[4]);
									int uncommonCost = Integer.parseInt(args[5]);
									int rareCost = Integer.parseInt(args[6]);
									int epicCost = Integer.parseInt(args[7]);
									int legendaryCost = Integer.parseInt(args[8]);
									if (commonCost + uncommonCost + rareCost + epicCost + legendaryCost > 0) {
										utils.setDustCost(args[2], itemIndex, commonCost, uncommonCost, rareCost,
												epicCost, legendaryCost);
										p.sendMessage(prefix + ChatColor.GREEN + "Item costs updated!");
									} else {
										p.sendMessage(prefix + ChatColor.RED
												+ "The item cannot have 0 cost for all dust types!");
									}
								} else {
									p.sendMessage(prefix + ChatColor.RED + "Item not found!");
								}
							} else {
								p.sendMessage(prefix + ChatColor.RED + "There are no items of that rarity!");
							}
						} else {
							p.sendMessage(prefix + ChatColor.RED
									+ "Please use a proper rarity: Common, Uncommon, Rare, Epic, Legendary");
							p.sendMessage(ChatColor.RED + "Rarities are case sensitive!");
						}
					} else {
						p.sendMessage(prefix + ChatColor.RED + "Usage: /Forge set cost (Rarity) (Slot) (Common cost) "
								+ "(Uncommon cost) (Rare cost) (Epic cost) (Legendary cost)");
					}
				} else if (args.length == 3 && args[0].equalsIgnoreCase("delete")) {
					if (p.hasPermission("Forge.Item.Delete")) {
						if (utils.getRarities().contains(args[1])) {
							if (configData.contains("Items." + args[1])) {
								ArrayList<ItemStack> items = (ArrayList<ItemStack>) configData
										.getList("Items." + args[1]);
								if (isInt(args[2])) {
									int index = Integer.parseInt(args[2]);
									if (items.size() >= index) {
										index--;
										items.remove(index);
										configData.set("Items." + args[1], items);
										int largest = index;
										configData.set("Costs." + args[1] + "." + index, null);
										for (String string : configData.getConfigurationSection("Costs." + args[1])
												.getKeys(false)) {
											int number = Integer.parseInt(string);
											if (number > largest) {
												largest = number;
											}
											if (number > index) {
												number--;
												configData.set("Costs." + args[1] + "." + Integer.toString(number),
														configData.get("Costs." + args[1] + "." + string));
											}
										}
										configData.set("Costs." + args[1] + "." + largest, null);
										utils.saveCustomConfig();
										p.sendMessage(prefix + ChatColor.GREEN + "Item removed!");
									} else {
										p.sendMessage(prefix + ChatColor.RED + "That slot is not filled!");
									}
								} else {
									p.sendMessage(prefix + ChatColor.RED
											+ "Usage: /Forge delete (Common, Uncommon, Rare, Epic, Legendary) (Item slot in inventory)");
									p.sendMessage(ChatColor.RED + "Rarities are case sensitive!");
								}
							} else {
								p.sendMessage(prefix + ChatColor.RED + "There are no items in the rarity!");
							}
						} else {
							p.sendMessage(prefix + ChatColor.RED
									+ "Usage: /Forge delete (Common, Uncommon, Rare, Epic, Legendary) (Item slot in inventory)");
							p.sendMessage(ChatColor.RED + "Rarities are case sensitive!");
						}
					} else {
						p.sendMessage(prefix + ChatColor.RED + "Missing permission: Forge.Item.Delete");
					}
				} else {
					p.sendMessage(prefix + "Commands");
					p.sendMessage("/Forge Warp - warp to the forge");
					if (p.hasPermission("Forge.Create") || p.isOp()) {
						p.sendMessage("/Forge Create - Creates the forge at your targeted block");
					}
					if (p.hasPermission("Forge.Set.Warp") || p.isOp()) {
						p.sendMessage("/Forge Create Warp - Creates the forge warp at your location");
					}
					if (p.hasPermission("Forge.Add") || p.isOp()) {
						p.sendMessage("/Forge Add (Rarity) - Add an item to The Forge with the rarity of your choice");
					}
					if (p.hasPermission("Forge.Item.Delete") || p.isOp()) {
						p.sendMessage(
								"/Forge Delete (Rarity) (Slot)- delete an item to The Forge for a specific rarity and slot.");
					}
					if (p.hasPermission("Forge.Set.Cost") || p.isOp()) {
						p.sendMessage(
								"/Forge set cost (Rarity) (Slot) (Common cost) (Uncommon cost) (Rare cost) (Epic cost) (Legendary cost)"
										+ " - Set the dust costs for a Forge item for a specific rarity and slot.");
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addForgeItem(ItemStack i, String rarity, Player player) {
		int index;
		ArrayList<ItemStack> list;
		if (configData.contains("Items." + rarity)) {
			ArrayList configList = (ArrayList<ItemStack>) configData.getList("Items." + rarity);
			list = (ArrayList<ItemStack>) configList.clone();
			list.add(i);
			index = list.size() - 1;
		} else {
			list = new ArrayList<>();
			list.add(i);
			index = 0;
		}
		if (index <= 44) {
			configData.set("Items." + rarity, list);
			utils.setDustCost(rarity, index, 1, 1, 1, 1, 1);
			utils.saveCustomConfig();
			player.sendMessage(prefix + ChatColor.GREEN + "Item added to the forge!");
			player.sendMessage(ChatColor.GREEN
					+ "Don't forget to set its dust cost with /Forge set cost (Rarity) (Slot) (Common cost) (Uncommon cost) (Rare cost) (Epic cost) (Legendary cost)");
			player.sendMessage(ChatColor.GREEN + "Put 0 to exclude a dust type from the cost");
			player.sendMessage(ChatColor.GREEN + "Dust costs default to 1 of each type of dust");
		} else {
			player.sendMessage(prefix + ChatColor.RED
					+ "That rarity is filled! (Max number of items 45, this will be changed to unlimited soon)");
		}
	}

	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			return false;
		}
		return true;
	}
}

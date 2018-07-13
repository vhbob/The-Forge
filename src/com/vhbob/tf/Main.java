package com.vhbob.tf;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.vhbob.tf.commands.CreateForge;
import com.vhbob.tf.handling.DustDrops;
import com.vhbob.tf.handling.ForgeHandling;
import com.vhbob.tf.handling.ForgeInvHandling;
import com.vhbob.tf.handling.ForgingHandling;

public class Main extends JavaPlugin {

	private File pluginData;
	private FileConfiguration dataConfig;

	@Override
	public void onEnable() {
		createCustomConfig();
		File configFile = new File(getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		getConfig().setDefaults(config);
		saveDefaultConfig();
		reloadConfig();
		getServer().getPluginManager().registerEvents(new ForgeHandling(this), this);
		getServer().getPluginManager().registerEvents(new DustDrops(this), this);
		getCommand("Forge").setExecutor(new CreateForge(this));
		getServer().getPluginManager().registerEvents(new ForgeInvHandling(this), this);
		getServer().getPluginManager().registerEvents(new ForgingHandling(this), this);
		getServer().getConsoleSender().sendMessage(prefix + "The Forge has been " + ChatColor.GREEN + "enabled!");
	}

	public FileConfiguration getCustomConfig() {
		return this.dataConfig;
	}

	private void createCustomConfig() {
		pluginData = new File(getDataFolder(), "pluginData.yml");
		if (!pluginData.exists()) {
			pluginData.getParentFile().mkdirs();
			saveResource("pluginData.yml", false);
		}

		dataConfig = new YamlConfiguration();
		try {
			dataConfig.load(pluginData);
		} catch (IOException | InvalidConfigurationException event) {
			event.printStackTrace();
		}
	}

	String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix")) + ChatColor.RESET;

}

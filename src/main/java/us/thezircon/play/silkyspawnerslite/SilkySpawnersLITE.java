package us.thezircon.play.silkyspawnerslite;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import us.thezircon.play.silkyspawnerslite.commands.CheckSpawner;
import us.thezircon.play.silkyspawnerslite.commands.SilkySpawner.Silky;
import us.thezircon.play.silkyspawnerslite.events.BreakSpawner;
import us.thezircon.play.silkyspawnerslite.events.PlaceSpawner;
import us.thezircon.play.silkyspawnerslite.events.ChangeSpawner;
import us.thezircon.play.silkyspawnerslite.nms.POST_1_17;
import us.thezircon.play.silkyspawnerslite.nms.nmsHandler;
import us.thezircon.play.silkyspawnerslite.utils.HexFormat;
import us.thezircon.play.silkyspawnerslite.utils.UpdateConfigs;

import java.io.File;
import java.io.IOException;

public final class SilkySpawnersLITE extends JavaPlugin {

    private static nmsHandler nms;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        //Create & Update Configs
        File configFile = new File(this.getDataFolder(), "config.yml");
        if (configFile.exists()) {
            UpdateConfigs.config();
        }
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        createLangConfig();
        UpdateConfigs.lang();

        //NMS Setup & Checks
        nms = new POST_1_17();

        //Check for vault
        if (!setupEconomy()) {
            //log.warning(String.format("[%s] - Some features will be disabled due to not having Vault installed!", getDescription().getName()));
            getServer().getConsoleSender().sendMessage(HexFormat.format("&8[&bSilky&6Spawners&8] &eSome features will be disabled due to not having Vault installed!"));
            if (getConfig().getBoolean("chargeOnBreak.enabled")) {
                getConfig().set("chargeOnBreak.enabled", false);
                saveConfig();
                reloadConfig();
            }
        }

        //Commands
        getCommand("silky").setExecutor(new Silky());
        getCommand("checkspawner").setExecutor(new CheckSpawner());

        //Events & Listeners
        getServer().getPluginManager().registerEvents(new BreakSpawner(), this);
        getServer().getPluginManager().registerEvents(new PlaceSpawner(), this);
        getServer().getPluginManager().registerEvents(new ChangeSpawner(), this);

        // Note: removed metrics/version checker
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static nmsHandler getNMS() {
        return nms;
    }

    //Lang.yml
    private File customLangFile;
    private FileConfiguration customLangConfig;

    private void createLangConfig() {
        customLangFile = new File(getDataFolder(), "lang.yml");
        if (!customLangFile.exists()) {
            customLangFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        customLangConfig= new YamlConfiguration();
        try {
            customLangConfig.load(customLangFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getLangConfig() {
        return this.customLangConfig;
    }

    public void langReload(){
        customLangConfig = YamlConfiguration.loadConfiguration(customLangFile);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

}

package br.ufpb.dcx.lima.albiere.fK_Balance.configs;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    private final JavaPlugin plugin;
    private final HashMap<String, File> files = new HashMap<>();
    private final HashMap<String, FileConfigurationExtends> configs = new HashMap<>();

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(String name) {
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            plugin.saveResource(name + ".yml", false);
        }

        FileConfigurationExtends config = new FileConfigurationExtends(YamlConfiguration.loadConfiguration(file));
        files.put(name, file);
        configs.put(name, config);
    }

    public FileConfigurationExtends getConfig(String name) {
        loadConfig(name);
        return configs.get(name);
    }

    public void saveConfig(String name) {
        if (!configs.containsKey(name)) return;
        try {
            configs.get(name).save(files.get(name));
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar " + name + ".yml");
            e.printStackTrace();
        }
    }

    public void reloadConfig(String name) {
        if (!files.containsKey(name)) return;

        File file = files.get(name);
        FileConfigurationExtends newConfig = new FileConfigurationExtends(YamlConfiguration.loadConfiguration(file));
        configs.put(name, newConfig);
        plugin.getLogger().info("Arquivo " + name + ".yml recarregado.");
    }

    public void reloadAllConfigs() {
        for (String name : configs.keySet()) {
            forceReload(name);
        }
    }

    public void forceReload(String name) {
        File file = new File(plugin.getDataFolder(), name + ".yml");
        if (!file.exists()) return;

        configs.remove(name);
        files.remove(name);

        FileConfigurationExtends newConfig = new FileConfigurationExtends(YamlConfiguration.loadConfiguration(file));
        files.put(name, file);
        configs.put(name, newConfig);
    }



}

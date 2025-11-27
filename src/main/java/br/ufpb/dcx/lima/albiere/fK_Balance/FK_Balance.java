package br.ufpb.dcx.lima.albiere.fK_Balance;

import br.ufpb.dcx.lima.albiere.fK_Balance.configs.Config;
import br.ufpb.dcx.lima.albiere.fK_Balance.configs.FileConfigurationExtends;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.ApiManager;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.ApiManagerInterface;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.EconomiesManager;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class FK_Balance extends JavaPlugin {

    private static FileConfigurationExtends config;
    private static FileConfigurationExtends messagesConfig;
    private static Config fileManager;
    private static FileConfigurationExtends economies;
    private static EconomiesManager economiesManager;
    private static String economyList;
    private static String prefix;
    private static FK_Balance plugin;

    public static FK_Balance getPlugin() {
        return plugin;
    }

    public static FileConfigurationExtends getMessagesConfig() {
        return messagesConfig;
    }

    public static FileConfigurationExtends getEconomies() {
        return economies;
    }

    @Override
    public void onEnable() {
        plugin = this;
        loadAllConfigs();
        loadEconomies();
        loadCommands();
        registerAPI();
    }

    @Override
    public void onDisable() {
        System.out.println(prefix + "Desligando...");
    }

    public void loadEconomies() {
        economiesManager = new EconomiesManager();
        economyList = String.join("|", getEconomies().getKeys(false));
        economiesManager.loadEconomyDefinitions();
        economiesManager.loadAllBalancesFromDatabase();
    }

    public static EconomiesManager getEconomiesManager() {
        return economiesManager;
    }

    public static @NotNull FileConfigurationExtends getConfigFile() {
        return config;
    }

    public static void loadAllConfigs() {
        fileManager = new Config(getPlugin());
        config = fileManager.getConfig("config");
        prefix = config.getColouredString("essentials.prefix");

        messagesConfig = fileManager.getConfig("messages");
        economies = fileManager.getConfig("economies");

        List<String> files = List.of("messages.yml", "config.yml");

        for(String file : files) {
            InputStreamReader messagesD = new InputStreamReader(Objects.requireNonNull(getPlugin().getResource(file)), StandardCharsets.UTF_8);
            FileConfigurationExtends messagesDefault = new FileConfigurationExtends(YamlConfiguration.loadConfiguration(messagesD));
            for (String key : messagesDefault.getKeys(false)) {
                if (!(file.equalsIgnoreCase("messages.yml") ? messagesConfig.contains(key) : config.contains(key))) {
                    (file.equalsIgnoreCase("messages.yml") ? messagesConfig : config).set(key, messagesDefault.getString(key));
                }
            }
        }

    }


    public void registerAPI() {
        ApiManager api = new ApiManager();
        getServer().getServicesManager().register(
                ApiManagerInterface.class,
                api,
                this,
                ServicePriority.Normal
        );
    }


    public void loadCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        String commandPackage = "br.ufpb.dcx.lima.albiere.fK_Balance.commands";

        Reflections reflections = new Reflections(commandPackage);
        Set<Class<? extends BaseCommand>> allCommands = reflections.getSubTypesOf(BaseCommand.class);
        commandManager.getCommandReplacements().addReplacement("economies", economyList);
        getLogger().info("Registrando " + allCommands.size() + " comandos...");
        for (Class<? extends BaseCommand> commandClass : allCommands) {
            try {
                BaseCommand commandInstance = commandClass.getDeclaredConstructor().newInstance();
                commandManager.registerCommand(commandInstance);
                getLogger().info("Comando '" + commandClass.getSimpleName() + "' registrado com sucesso!");
            } catch (Exception e) {
                getLogger().severe("Não foi possível registrar o comando " + commandClass.getSimpleName());
                e.printStackTrace();
            }
        }
    }


}

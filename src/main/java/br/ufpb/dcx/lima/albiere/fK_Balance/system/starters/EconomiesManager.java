package br.ufpb.dcx.lima.albiere.fK_Balance.system.starters;

import br.ufpb.dcx.lima.albiere.fK_Balance.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Balance.configs.mysql.SQLConfig;
import br.ufpb.dcx.lima.albiere.fK_Balance.exeptions.MissingEconomySettingsException;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.*;

public class EconomiesManager {

    private final Map<String, Economy> economyDefinitions = new HashMap<>();

    private Map<UUID, Map<String, BigDecimal>> playerBalances = new HashMap<>();

    private final SQLConfig sqlConfig;

    public EconomiesManager() {
        sqlConfig = new SQLConfig();
        sqlConfig.createTableIfNotExists();
        loadEconomyDefinitions();
        loadAllBalancesFromDatabase();
    }

    public void loadEconomyDefinitions() {
        economyDefinitions.clear();

        ConfigurationSection economiesSection = FK_Balance.getEconomies();
        if (economiesSection == null) {
            System.err.println(" Nada encontrado na 'economies.yml'. Nenhuma economia será carregada.");
            return;
        }

        Set<String> economyKeys = economiesSection.getKeys(false);

        try {
            for (String key : economyKeys) {
                String prefix = economiesSection.getString(key + ".prefixBalance", "$");
                Economy economy = new Economy(key, prefix);
                this.economyDefinitions.put(key, economy);
            }


            System.out.println(" " + economyDefinitions.size() + " definições de economia foram carregadas do economies.yml.");
        } catch (Exception e) {
            throw new MissingEconomySettingsException("Estão faltando configurações em alguma economia. Verifique: economies.yml");
        }
    }

    public Optional<Economy> getEconomy(String name) {
        return Optional.ofNullable(economyDefinitions.get(name.toLowerCase()));
    }

    public Collection<Economy> getAllEconomies() {
        return economyDefinitions.values();
    }

    public void loadAllBalancesFromDatabase() {
        System.out.println("Iniciando carregamento de saldos do MySQL...");
        this.playerBalances = sqlConfig.loadAllBalances();
        System.out.println(playerBalances.size() + " jogadores com saldos foram carregados.");
    }

    public void saveAllBalancesToDatabase() {
        System.out.println("Iniciando salvamento de saldos para o MySQL...");
        sqlConfig.saveAllBalancesBatch(this.playerBalances);
    }

    public BigDecimal getBalance(UUID playerUuid, String economyName) {
        return playerBalances.getOrDefault(playerUuid, Collections.emptyMap())
                .getOrDefault(economyName.toLowerCase(), BigDecimal.ZERO);
    }

    public void setBalance(UUID playerUuid, String economyName, BigDecimal newBalance) {
        // Garante que o mapa interno para o jogador exista
        this.playerBalances.computeIfAbsent(playerUuid, k -> new HashMap<>());
        // Define o novo saldo
        this.playerBalances.get(playerUuid).put(economyName.toLowerCase(), newBalance);
    }

    public boolean hasAccount(UUID playerUuid) {
        return playerBalances.containsKey(playerUuid);
    }
}
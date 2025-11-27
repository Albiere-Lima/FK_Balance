package br.ufpb.dcx.lima.albiere.fK_Balance.system;

import br.ufpb.dcx.lima.albiere.fK_Balance.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.EconomiesManager;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.Economy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class ApiManager implements ApiManagerInterface {


    private final EconomiesManager manager;

    public ApiManager() {
        manager = FK_Balance.getEconomiesManager();
    }


    public Optional<Economy> getEconomy(String name) {
        return manager.getEconomy(name);
    }

    public BigDecimal getBalance(UUID uuid, String economyName) {
        return manager.getBalance(uuid, economyName);
    }

    public void setBalance(UUID playerUuid, String economyName, BigDecimal newBalance) {
        manager.setBalance(playerUuid, economyName, newBalance);
    }

    public boolean hasAccount(UUID playerUuid) {
        return manager.hasAccount(playerUuid);
    }

    public Collection<Economy> getAllEconomies() {
        return manager.getAllEconomies();
    }
}

package br.ufpb.dcx.lima.albiere.fK_Balance.system;

import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.Economy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ApiManagerInterface {

    Optional<Economy> getEconomy(String name);
    Collection<Economy> getAllEconomies();
    BigDecimal getBalance(UUID uuid, String economyName);
    void setBalance(UUID playerUuid, String economyName, BigDecimal newBalance);
    boolean hasAccount(UUID playerUuid);

}

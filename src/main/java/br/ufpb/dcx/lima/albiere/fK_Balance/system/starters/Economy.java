package br.ufpb.dcx.lima.albiere.fK_Balance.system.starters;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Economy {

    private final String name;
    private String prefix;
    private Map<UUID, BigDecimal> balances = new HashMap<>();

    public Economy(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }


    public BigDecimal getBalance(UUID playerUUID) {
        return balances.getOrDefault(playerUUID, BigDecimal.ZERO);
    }

    public void setBalance(UUID playerUUID, BigDecimal amount) {
        balances.put(playerUUID, amount);
    }

    public void deposit(UUID playerUUID, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(playerUUID);
        setBalance(playerUUID, currentBalance.add(amount));
    }

    public boolean withdraw(UUID playerUUID, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(playerUUID);
        if (currentBalance.compareTo(amount) >= 0) {
            setBalance(playerUUID, currentBalance.subtract(amount));
            return true;
        }
        return false;
    }

    public boolean hasAccount(UUID playerUUID) {
        return balances.containsKey(playerUUID);
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Map<UUID, BigDecimal> getAllBalances() {
        return balances;
    }

    public void setAllBalances(Map<UUID, BigDecimal> balances) {
        this.balances = balances;
    }
}
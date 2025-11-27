package br.ufpb.dcx.lima.albiere.fK_Balance.configs.mysql;

import br.ufpb.dcx.lima.albiere.fK_Balance.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.EconomiesManager;
import br.ufpb.dcx.lima.albiere.fK_Balance.system.starters.Economy;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLConfig {

    private final String tableName;

    public SQLConfig() {

        this.tableName = FK_Balance.getConfigFile().getString("sql.table", "fk_balances");
    }

    /**
     * Cria a nova tabela de economias, se ela não existir.
     * A tabela é projetada para suportar múltiplas economias de forma flexível.
     */
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "`player_uuid` VARCHAR(36) NOT NULL," +
                "`economy_name` VARCHAR(100) NOT NULL," +
                "`balance` DECIMAL(19, 4) NOT NULL DEFAULT 0.0000," +
                "PRIMARY KEY (`player_uuid`, `economy_name`)" +
                ");";

        try (Connection conn = ConectionSQL.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println(" Tabela de economias do MySQL verificada/criada com sucesso!");

        } catch (SQLException e) {
            System.err.println(" ERRO FATAL ao tentar criar a tabela de economias!");
            e.printStackTrace();
        }
    }


    public Map<String, BigDecimal> loadAllBalances(UUID playerUuid) {
        Map<String, BigDecimal> playerEconomies = new HashMap<>();
        String sql = "SELECT economy_name, balance FROM `" + tableName + "` WHERE player_uuid = ?;";

        try (Connection conn = ConectionSQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerUuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String economyName = rs.getString("economy_name");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    playerEconomies.put(economyName, balance);
                }
            }
        } catch (SQLException e) {
            System.err.println("Falha ao carregar os saldos do UUID " + playerUuid);
            e.printStackTrace();
        }
        return playerEconomies;
    }


    public void savePlayerBalance(UUID playerUuid, String economyName, BigDecimal balance) {
        String sql = "INSERT INTO `" + tableName + "` (player_uuid, economy_name, balance) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE balance = VALUES(balance);";

        try (Connection conn = ConectionSQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerUuid.toString());
            pstmt.setString(2, economyName);
            pstmt.setBigDecimal(3, balance);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Falha ao salvar o saldo para o UUID " + playerUuid + " na economia " + economyName);
            e.printStackTrace();
        }
    }


    public void saveAllBalances(EconomiesManager economiesManager) {
        String sql = "INSERT INTO " + tableName + " (player_uuid, economy_name, balance) VALUES (?,?,?) " +
                "ON DUPLICATE KEY UPDATE balance = VALUES(balance)";

        try (Connection conn = ConectionSQL.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            int batchCount = 0;
            for (Economy economy : economiesManager.getAllEconomies()) {
                for (Map.Entry<UUID, BigDecimal> entry : economy.getAllBalances().entrySet()) {
                    pstmt.setString(1, entry.getKey().toString());
                    pstmt.setString(2, economy.getName());
                    pstmt.setBigDecimal(3, entry.getValue());
                    pstmt.addBatch();
                    batchCount++;
                }
            }

            pstmt.executeBatch();
            conn.commit();
            System.out.println(" " + batchCount + " registros de saldo salvos no MySQL.");

        } catch (SQLException e) {
            System.err.println(" Falha ao salvar os saldos no MySQL.");
            e.printStackTrace();
        }

    }
        public void saveAllBalancesBatch(Map<UUID, Map<String, BigDecimal>> allBalances) {
            String sql = "INSERT INTO `" + tableName + "` (player_uuid, economy_name, balance) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE balance = VALUES(balance);";

            try (Connection conn = ConectionSQL.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                int batchSize = 0;
                for (Map.Entry<UUID, Map<String, BigDecimal>> playerEntry : allBalances.entrySet()) {
                    UUID playerUuid = playerEntry.getKey();
                    for (Map.Entry<String, BigDecimal> economyEntry : playerEntry.getValue().entrySet()) {
                        pstmt.setString(1, playerUuid.toString());
                        pstmt.setString(2, economyEntry.getKey());
                        pstmt.setBigDecimal(3, economyEntry.getValue());
                        pstmt.addBatch();
                        batchSize++;


                        if (batchSize % 1000 == 0) {
                            pstmt.executeBatch();
                        }
                    }
                }
                pstmt.executeBatch();
                System.out.println(" Todos os saldos de economias foram salvos no MySQL!");

            } catch (SQLException e) {
                System.err.println("Falha ao salvar todos os saldos em lote.");
                e.printStackTrace();
            }
    }

    public Map<UUID, Map<String, BigDecimal>> loadAllBalances() {
        Map<UUID, Map<String, BigDecimal>> allBalances = new HashMap<>();
        String sql = "SELECT player_uuid, economy_name, balance FROM `" + tableName + "`;";

        try (Connection conn = ConectionSQL.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UUID playerUuid = UUID.fromString(rs.getString("player_uuid"));
                String economyName = rs.getString("economy_name");
                BigDecimal balance = rs.getBigDecimal("balance");

                allBalances.computeIfAbsent(playerUuid, k -> new HashMap<>()).put(economyName, balance);
            }
            System.out.println(" Todos os saldos de economias foram carregados do MySQL!");

        } catch (SQLException e) {
            System.err.println("Falha ao carregar todos os saldos do servidor.");
            e.printStackTrace();
        }
        return allBalances;
    }
}
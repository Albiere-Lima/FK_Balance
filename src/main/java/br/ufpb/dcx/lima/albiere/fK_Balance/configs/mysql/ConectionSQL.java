package br.ufpb.dcx.lima.albiere.fK_Balance.configs.mysql;

import br.ufpb.dcx.lima.albiere.fK_Balance.FK_Balance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionSQL {

    private static final String ip = FK_Balance.getConfigFile().getString("mysql.ip");
    private static final String database = FK_Balance.getConfigFile().getString("mysql.database");
    private static final String URL = "jdbc:mysql://"+ip+":3306/"+database+"?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = FK_Balance.getConfigFile().getString("mysql.user");
    private static final String SENHA = FK_Balance.getConfigFile().getString("mysql.password");

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage(), e);
        }
    }
}

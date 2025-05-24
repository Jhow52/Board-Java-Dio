package org.example.persistence.config;


import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;


@NoArgsConstructor(access = PRIVATE) // Gera um construtor sem argumentos privado, impedindo criação de instâncias da classe
public final class ConnectionConfig { // Classe final, não pode ser estendida

    public static Connection getConnection() throws SQLException { // Método estático para obter conexão com banco
        var url = "jdbc:mysql://localhost/board"; // URL do banco MySQL local, banco chamado "board"
        var user = "root";                         // Usuário do banco
        var password = "root";                     // Senha do banco
        var connection = DriverManager.getConnection(url, user, password); // Abre conexão JDBC
        connection.setAutoCommit(false);           // Desabilita auto commit para controle manual de transações
        return connection;                         // Retorna a conexão criada
    }
}
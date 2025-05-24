package org.example.persistence.migration;


import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

import static org.example.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor // Lombok cria um construtor com todos os campos (aqui só 'connection')
public class MigrationStrategy {

    private final Connection connection; // Conexão com o banco, recebida via construtor

    public void executeMigration(){
        // Salva referências das saídas padrão e erro para restaurar depois da execução
        var originalOut = System.out;
        var originalErr = System.err;

        try(var fos = new FileOutputStream("liquibase.log")){ // Abre (cria) arquivo para salvar log do Liquibase
            System.setOut(new PrintStream(fos)); // Redireciona saída padrão para o arquivo de log
            System.setErr(new PrintStream(fos)); // Redireciona saída de erro para o arquivo de log

            try(
                    var connection = getConnection(); // **ATENÇÃO:** Abre nova conexão local, que pode conflitar com o atributo da classe
                    var jdbcConnection = new JdbcConnection(connection); // Envolve a conexão JDBC para o Liquibase
            ){
                // Cria objeto Liquibase apontando para arquivo master do changelog dentro do classpath
                var liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.yml",
                        new ClassLoaderResourceAccessor(),
                        jdbcConnection
                );

                liquibase.update(); // Executa as migrations pendentes no banco
            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace(); // Imprime stack trace dos erros (irá para o arquivo liquibase.log)
                System.setErr(originalErr); // Restaura saída padrão de erro caso aconteça exceção
            }
        } catch (IOException ex){
            ex.printStackTrace(); // Caso tenha problema ao abrir o arquivo liquibase.log
        } finally {
            System.setOut(originalOut); // Garante que a saída padrão seja restaurada no final
            System.setErr(originalErr); // Garante que a saída de erro seja restaurada no final
        }
    }
}
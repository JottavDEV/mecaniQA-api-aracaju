package br.com.mecaniQA.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Ponto único de acesso ao MySQL via JDBC nativo (java.sql).
// Cada chamada a abrir() devolve uma conexão nova, que quem chamou
// deve fechar (try-with-resources nos repositórios).
@Component
public class ConexaoFactory {

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoFactory(
            @Value("${db.url}") String url,
            @Value("${db.username}") String usuario,
            @Value("${db.password}") String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}

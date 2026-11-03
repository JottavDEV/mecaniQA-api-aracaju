package br.com.mecaniQA.api.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

// Contrato dos Mappers de ResultSet: lê a linha atual do cursor
// (rs.next() já foi chamado por quem usa) e devolve o objeto de modelo.
public interface ResultSetMapper<T> {

    T mapear(ResultSet rs) throws SQLException;
}

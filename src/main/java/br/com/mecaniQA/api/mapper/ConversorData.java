package br.com.mecaniQA.api.mapper;

import java.sql.Timestamp;
import java.time.LocalDateTime;

// Conversões seguras (null-safe) entre Timestamp (JDBC) e LocalDateTime (modelo).
public class ConversorData {

    private ConversorData() {
        // classe utilitária: não deve ser instanciada
    }

    public static LocalDateTime paraLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static Timestamp paraTimestamp(LocalDateTime dataHora) {
        return dataHora == null ? null : Timestamp.valueOf(dataHora);
    }
}

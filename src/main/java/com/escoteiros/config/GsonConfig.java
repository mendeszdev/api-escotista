package com.escoteiros.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Gson configurado com adaptadores para os tipos Java que o banco usa:
 *   - UUID  (chaves primárias e estrangeiras)
 *   - LocalDate  (datas simples: data_nascimento, data_limite…)
 *   - OffsetDateTime (timestamps com timezone: criado_em, atualizado_em…)
 */
public class GsonConfig {

    private static final Gson INSTANCE = new GsonBuilder()
        .serializeNulls()

        // UUID ↔ String
        .registerTypeAdapter(UUID.class,
            (JsonDeserializer<UUID>) (json, t, ctx) ->
                UUID.fromString(json.getAsString()))
        .registerTypeAdapter(UUID.class,
            (JsonSerializer<UUID>) (src, t, ctx) ->
                new JsonPrimitive(src.toString()))

        // LocalDate ↔ "YYYY-MM-DD"
        .registerTypeAdapter(LocalDate.class,
            (JsonDeserializer<LocalDate>) (json, t, ctx) ->
                LocalDate.parse(json.getAsString()))
        .registerTypeAdapter(LocalDate.class,
            (JsonSerializer<LocalDate>) (src, t, ctx) ->
                new JsonPrimitive(src.toString()))

        // OffsetDateTime ↔ ISO-8601
        .registerTypeAdapter(OffsetDateTime.class,
            (JsonDeserializer<OffsetDateTime>) (json, t, ctx) ->
                OffsetDateTime.parse(json.getAsString()))
        .registerTypeAdapter(OffsetDateTime.class,
            (JsonSerializer<OffsetDateTime>) (src, t, ctx) ->
                new JsonPrimitive(src.toString()))

        .create();

    public static Gson get() { return INSTANCE; }

    private GsonConfig() {}
}

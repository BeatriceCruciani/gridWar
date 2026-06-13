package it.unicam.cs.mpgc.rpg123743.repository;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg123743.model.Item;
import java.lang.reflect.Type;

/**
 * Adapter personalizzato per la serializzazione/deserializzazione polimorfica di Item.
 * Utilizza le reflection di Java (Class.forName) per istanziare la sottoclasse corretta
 * leggendo il campo "itemType" dal JSON (es. "Weapon" → Weapon.class).
 * Usa un Gson interno senza adapter per evitare ricorsione infinita.
 */
public class ItemTypeAdapter implements JsonDeserializer<Item>, JsonSerializer<Item> {

    private static final String TYPE_FIELD    = "itemType";
    private static final String MODEL_PACKAGE = "it.unicam.cs.mpgc.rpg123743.model.";

    // Gson interno senza l'adapter — evita la ricorsione infinita
    private final Gson internalGson = new Gson();

    /**
     * Serializza un Item aggiungendo il campo "itemType" con il nome semplice della classe.
     * Esempio: una Weapon diventa "itemType": "Weapon"
     */
    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = internalGson.toJsonTree(src, src.getClass()).getAsJsonObject();
        jsonObject.addProperty(TYPE_FIELD, src.getClass().getSimpleName());
        return jsonObject;
    }

    /**
     * Deserializza un Item leggendo il campo "itemType" e usando Class.forName
     * per istanziare la sottoclasse corretta tramite reflection.
     *
     * @throws JsonParseException se il campo "itemType" manca o la classe non esiste.
     */
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has(TYPE_FIELD)) {
            throw new JsonParseException("Missing '" + TYPE_FIELD + "' field in Item JSON.");
        }

        String typeName = jsonObject.get(TYPE_FIELD).getAsString();

        try {
            Class<?> clazz = Class.forName(MODEL_PACKAGE + typeName);
            return (Item) internalGson.fromJson(jsonObject, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown Item type: " + typeName, e);
        }
    }
}
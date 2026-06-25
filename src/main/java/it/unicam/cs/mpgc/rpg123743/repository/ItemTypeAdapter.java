package it.unicam.cs.mpgc.rpg123743.repository;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg123743.model.Item;
import java.lang.reflect.Type;

/**
 * Adattatore polimorfico per la serializzazione di oggetti Item.
 * Salva il nome della classe concreta nel JSON per permettere la corretta
 * deserializzazione degli oggetti astratti (Weapon, HealingPotion, ecc.).
 *
 * Utilizza un'istanza Gson interna "grezza", priva dell'adapter polimorfico
 * registrato, per serializzare/deserializzare i campi specifici della classe
 * concreta. Questo evita una ricorsione infinita: dato che l'adapter è
 * registrato tramite {@code registerTypeHierarchyAdapter} su tutta la gerarchia
 * di {@code Item}, usare il {@code JsonSerializationContext} principale per
 * serializzare i dettagli richiamerebbe nuovamente questo stesso adapter,
 * causando uno {@link StackOverflowError}.
 */
public class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    private static final String CLASS_META_KEY = "type";
    private static final String PROPERTIES_KEY = "properties";

    /**
     * Gson "grezzo", senza alcun type adapter polimorfico registrato.
     * Usato esclusivamente per (de)serializzare i campi specifici della
     * sottoclasse concreta, evitando di richiamare ricorsivamente questo adapter.
     */
    private final Gson delegateGson = new Gson();

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty(CLASS_META_KEY, src.getClass().getName());
        result.add(PROPERTIES_KEY, delegateGson.toJsonTree(src, src.getClass()));
        return result;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement typeElement = jsonObject.get(CLASS_META_KEY);
        if (typeElement == null) {
            throw new JsonParseException(
                    "Save file is missing the '" + CLASS_META_KEY + "' metadata for an item. "
                            + "This usually means the save was created with an older, incompatible "
                            + "version of GridWar. Delete the old save file and create a new one.");
        }
        String className = typeElement.getAsString();

        JsonElement propertiesElement = jsonObject.get(PROPERTIES_KEY);
        if (propertiesElement == null) {
            throw new JsonParseException(
                    "Save file is missing the '" + PROPERTIES_KEY + "' data for item of type " + className + ".");
        }

        try {
            Class<?> clazz = Class.forName(className);
            return (Item) delegateGson.fromJson(propertiesElement, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe non trovata durante la deserializzazione: " + className, e);
        }
    }
}
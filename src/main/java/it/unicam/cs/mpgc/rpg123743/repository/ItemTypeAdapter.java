package it.unicam.cs.mpgc.rpg123743.repository;

import com.google.gson.*;
import it.unicam.cs.mpgc.rpg123743.model.Item;
import java.lang.reflect.Type;

/**
 * Adattatore polimorfico per la serializzazione di oggetti Item.
 * Salva il nome della classe concreta nel JSON per permettere la corretta
 * deserializzazione degli oggetti astratti (Weapon, HealingPotion, ecc.).
 *
 * <p>Utilizza un'istanza Gson interna "grezza", priva dell'adapter polimorfico
 * registrato, per serializzare/deserializzare i campi specifici della classe
 * concreta. Questo evita una ricorsione infinita: dato che l'adapter è
 * registrato tramite {@code registerTypeHierarchyAdapter} su tutta la gerarchia
 * di {@code Item}, usare il {@code JsonSerializationContext} principale per
 * serializzare i dettagli richiamerebbe nuovamente questo stesso adapter,
 * causando uno {@link StackOverflowError}.</p>
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
        String className = jsonObject.get(CLASS_META_KEY).getAsString();
        try {
            Class<?> clazz = Class.forName(className);
            return (Item) delegateGson.fromJson(jsonObject.get(PROPERTIES_KEY), clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe non trovata durante la deserializzazione: " + className, e);
        }
    }
}
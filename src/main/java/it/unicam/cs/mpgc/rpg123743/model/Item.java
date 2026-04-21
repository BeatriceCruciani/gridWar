package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta un oggetto generico che un'unità può portare nel proprio inventario.
 * Gli oggetti hanno una durabilità limitata (numero di utilizzi rimanenti).
 * Una durabilità pari a -1 indica che l'oggetto ha utilizzi infiniti.
 * I tipi concreti di oggetto devono estendere questa classe.
 */
public abstract class Item {

    private final String name;
    private final String description;
    private int durability;

    /**
     * Costruisce un nuovo oggetto con nome, descrizione e durabilità specificati.
     *
     * @param name        il nome dell'oggetto (non nullo né vuoto).
     * @param description la descrizione dell'oggetto.
     * @param durability  il numero di utilizzi disponibili (-1 per infiniti).
     * @throws IllegalArgumentException se il nome è nullo o vuoto.
     */
    protected Item(String name, String description, int durability) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name must not be blank.");
        }
        this.name = name;
        this.description = description;
        this.durability = durability;
    }

    /** Restituisce il nome dell'oggetto. */
    public String getName() {
        return name;
    }

    /** Restituisce la descrizione dell'oggetto. */
    public String getDescription() {
        return description;
    }

    /** Restituisce il numero di utilizzi rimanenti (-1 se infiniti). */
    public int getDurability() {
        return durability;
    }

    /**
     * Restituisce {@code true} se questo oggetto ha durabilità infinita.
     */
    public boolean isUnbreakable() {
        return durability == -1;
    }

    /**
     * Restituisce {@code true} se questo oggetto ha ancora utilizzi disponibili.
     */
    public boolean isUsable() {
        return isUnbreakable() || durability > 0;
    }

    /**
     * Consuma un utilizzo di questo oggetto.
     * Non ha effetto sugli oggetti con durabilità infinita.
     */
    public void use() {
        if (!isUnbreakable() && durability > 0) {
            durability--;
        }
    }
}
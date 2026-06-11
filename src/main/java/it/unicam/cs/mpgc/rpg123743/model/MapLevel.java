package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta i livelli di mappa disponibili nel gioco.
 * Ogni livello ha un nome visualizzato e una descrizione della difficoltà.
 */
public enum MapLevel {

    /** Prima mappa — pianura aperta, 4 nemici, facile. */
    ASHBORNE_PLAINS("Ashborne Plains", "Easy - 4 enemies"),

    /** Seconda mappa — assedio al forte, 7 nemici, media difficoltà. */
    FORT_SIEGE("Fort Siege", "Medium - 7 enemies"),

    /**Terza mappa — passo montano, 12 nemici, difficile*/
    FROZEN_PASS("Frozen Pass", "Hard - 12 enemies");

    private final String displayName;
    private final String description;

    MapLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
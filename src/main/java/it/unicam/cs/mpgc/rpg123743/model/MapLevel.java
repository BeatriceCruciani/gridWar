package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta i livelli di mappa disponibili nel gioco GridWar.
 * Ogni costante definisce i metadati di configurazione del livello, inclusi
 * i dettagli visualizzabili nella UI e i parametri strutturali della griglia.
 */
public enum MapLevel {

    /** Prima mappa — pianura aperta, griglia compatta 8x8, facile. */
    ASHBORNE_PLAINS("Ashborne Plains", "Open plains, defy the enemy units.", 8, 8),

    /** Seconda mappa — assedio al forte, griglia media 12x12, difficoltà intermedia. */
    WALL_BREACH("Wall Breach", "Breach the walls of a fortified stronghold to defy the enemy.", 12, 12),

    /** Terza mappa — passo montano, griglia ampia 16x16, difficile. */
    TANGLED_HIGHLANDS("Tangled Highlands", "A mixed terrain map with a horde of enemies, use your best strategy.", 16, 16);

    private final String displayName;
    private final String description;
    private final int width;
    private final int height;

    MapLevel(String displayName, String description, int width, int height) {
        this.displayName = displayName;
        this.description = description;
        this.width = width;
        this.height = height;
    }

    /** @return il nome del livello da visualizzare a schermo. */
    public String getDisplayName() { return displayName; }

    /** @return la descrizione inglese degli obiettivi o del contesto del livello. */
    public String getDescription() { return description; }

    /** @return la larghezza della griglia per questo livello. */
    public int getWidth() { return width; }

    /** @return l'altezza della griglia per questo livello. */
    public int getHeight() { return height; }
}
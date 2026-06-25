package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta i livelli di mappa disponibili nel gioco GridWar.
 * Ogni costante definisce i metadati di configurazione del livello, inclusi i dettagli
 * visualizzabili nella UI (in inglese) e i parametri strutturali necessari al generatore della mappa.
 */
public enum MapLevel {

    /** Prima mappa — pianura aperta, griglia compatta 8x8, facile. */
    ASHBORNE_PLAINS("Ashborne Plains", "Open plains, defy the enemy units.", 8, 8, 4),

    /** Seconda mappa — assedio al forte, griglia media 12x12, difficoltà intermedia. */
    WALL_BREACH("Wall Breach", "Breach the walls of a fortified stronghold to defy the enemy.", 12, 12, 7),

    /** Terza mappa — passo montano, griglia ampia 16x16, difficile. */
    TANGLED_HIGHLANDS("Tangled Higlands", "A mixed terrain map with horde of enemies, use your best strategy to defy the enemy units.", 16, 16, 12);

    private final String displayName;
    private final String description;
    private final int width;
    private final int height;
    private final int enemyCount;

    /**
     * Costruttore interno per mappare la configurazione di ciascun livello di gioco.
     */
    MapLevel(String displayName, String description, int width, int height, int enemyCount) {
        this.displayName = displayName;
        this.description = description;
        this.width = width;
        this.height = height;
        this.enemyCount = enemyCount;
    }

    /** @return il nome del livello da visualizzare a schermo. */
    public String getDisplayName() { return displayName; }

    /** @return la descrizione inglese degli obiettivi o del contesto del livello. */
    public String getDescription() { return description; }

    /** @return la larghezza della griglia per questo livello. */
    public int getWidth() { return width; }

    /** @return l'altezza della griglia per questo livello. */
    public int getHeight() { return height; }

    /** @return il numero di nemici da spawnare nella mappa. */
    public int getEnemyCount() { return enemyCount; }
}
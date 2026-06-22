package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta un oggetto consumabile generico (es. pozioni, pergamene, esplosivi).
 * Questa classe è progettata seguendo il principio Open/Closed (OCP): definisce la logica
 * comune di gestione delle cariche (durabilità), ma delega il comportamento specifico dell'effetto
 * alle sue sottoclassi tramite il metodo astratto {@link #applyEffect(Unit)}.
 *
 * <p>In questo modo è possibile integrare future tipologie di consumabili (es. pozioni di mana, buff)
 * senza dover modificare il codice di questa classe.</p>
 */
public abstract class Consumable extends Item {

    /**
     * Costruisce un nuovo oggetto consumabile.
     *
     * @param name        il nome dell'oggetto.
     * @param description la descrizione dell'oggetto.
     * @param durability  il numero di utilizzi (cariche) disponibili inizialmente.
     * @throws IllegalArgumentException se la durabilità è minore di -1 (vedi {@link Item}).
     */
    public Consumable(String name, String description, int durability) {
        super(name, description, durability);
    }

    /**
     * Applica l'effetto del consumabile sull'unità bersaglio specificata e consuma
     * una carica dell'oggetto. La validazione e il decremento della durabilità sono
     * delegati a {@link Item#use()}, evitando di duplicare quella logica qui.
     *
     * @param target l'unità che subisce o beneficia dell'effetto del consumabile.
     * @throws IllegalStateException se l'oggetto è già esaurito (durabilità {@code <=} 0).
     */
    public void useOn(Unit target) {
        use();
        applyEffect(target);
    }

    /**
     * Metodo protetto da implementare nelle sottoclassi per definire l'effetto reale
     * del consumabile (es. curare, conferire un buff, infliggere danno).
     *
     * @param target l'unità su cui applicare l'effetto.
     */
    protected abstract void applyEffect(Unit target);
}
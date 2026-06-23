package it.unicam.cs.mpgc.rpg123743.repository;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import java.util.List;
import java.util.Optional;

/**
 * Definisce il contratto per la persistenza e il recupero dello stato di gioco di GridWar.
 * Le implementazioni possono utilizzare diversi backend di storage (JSON, database, ecc.)
 * senza influenzare il resto dell'applicazione (Disaccoppiamento tramite Interfacce).
 */
public interface GameRepository {

    /**
     * Persiste lo stato di gioco specificato.
     * L'implementazione estrarrà il nome del file direttamente dai metadati del GameState.
     *
     * @param state lo stato di gioco da salvare (non nullo).
     * @throws NullPointerException se state è nullo.
     */
    void save(GameState state);

    /**
     * Carica uno stato di gioco precedentemente salvato tramite il suo nome.
     *
     * @param saveName il nome del salvataggio da caricare (non nullo né vuoto).
     * @return un Optional contenente il GameState se trovato, vuoto altrimenti.
     * @throws IllegalArgumentException se saveName è nullo o composto da soli spazi vuoti.
     */
    Optional<GameState> load(String saveName);

    /**
     * Restituisce la lista di tutti i nomi dei salvataggi disponibili nel sistema.
     *
     * @return lista dei nomi dei salvataggi, vuota se non esistono salvataggi.
     */
    List<String> listSaves();

    /**
     * Elimina il salvataggio con il nome specificato.
     *
     * @param saveName il nome del salvataggio da eliminare (non nullo né vuoto).
     * @throws IllegalArgumentException se saveName è nullo o vuoto.
     */
    void delete(String saveName);
}
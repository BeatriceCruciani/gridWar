package it.unicam.cs.mpgc.rpg123743.repository;

import it.unicam.cs.mpgc.rpg123743.model.GameState;

import java.util.List;
import java.util.Optional;

/**
 * Definisce il contratto per la persistenza e il recupero dello stato di gioco.
 * Le implementazioni possono utilizzare diversi backend di storage (JSON, database, cloud, ecc.)
 * senza influenzare il resto dell'applicazione.
 */
public interface IGameRepository {

    /**
     * Persiste lo stato di gioco specificato.
     *
     * @param state lo stato di gioco da salvare.
     */
    void save(GameState state);

    /**
     * Carica uno stato di gioco precedentemente salvato tramite il suo nome.
     *
     * @param saveName il nome del salvataggio da caricare.
     * @return un Optional contenente il GameState se trovato, vuoto altrimenti.
     */
    Optional<GameState> load(String saveName);

    /**
     * Restituisce la lista di tutti i nomi dei salvataggi disponibili.
     *
     * @return lista dei nomi dei salvataggi, possibilmente vuota.
     */
    List<String> listSaves();

    /**
     * Elimina il salvataggio con il nome specificato.
     *
     * @param saveName il nome del salvataggio da eliminare.
     */
    void delete(String saveName);
}
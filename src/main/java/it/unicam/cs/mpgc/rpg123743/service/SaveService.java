package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.repository.GameRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Fornisce le operazioni di salvataggio e caricamento al resto dell'applicazione.
 * Funge da facade sul GameRepository, mantenendo la UI e gli altri service
 * disaccoppiati dai dettagli della persistenza.
 */
public class SaveService {

    private final GameRepository repository;

    /**
     * Costruisce un nuovo SaveService con il repository specificato.
     *
     * @param repository il repository da usare per la persistenza (non nullo).
     * @throws NullPointerException se repository è nullo.
     */
    public SaveService(GameRepository repository) {
        this.repository = Objects.requireNonNull(repository, "GameRepository must not be null.");
    }

    /**
     * Salva lo stato di gioco corrente.
     *
     * @param state lo stato di gioco da persistere (non nullo).
     * @throws NullPointerException se lo stato è nullo.
     */
    public void save(GameState state) {
        Objects.requireNonNull(state, "GameState cannot be null during save operation.");
        repository.save(state);
    }

    /**
     * Carica uno stato di gioco tramite il nome del salvataggio.
     *
     * @param saveName il nome del salvataggio da caricare (non nullo).
     * @return un Optional contenente il GameState caricato, vuoto se non trovato.
     * @throws NullPointerException se saveName è nullo.
     */
    public Optional<GameState> load(String saveName) {
        Objects.requireNonNull(saveName, "Save name cannot be null during load operation.");
        return repository.load(saveName);
    }

    /**
     * Restituisce la lista di tutti i nomi dei salvataggi disponibili.
     *
     * @return lista dei nomi dei salvataggi.
     */
    public List<String> listSaves() {
        return repository.listSaves();
    }

    /**
     * Elimina un salvataggio tramite il suo nome.
     *
     * @param saveName il nome del salvataggio da eliminare (non nullo).
     * @throws NullPointerException se saveName è nullo.
     */
    public void delete(String saveName) {
        Objects.requireNonNull(saveName, "Save name cannot be null during delete operation.");
        repository.delete(saveName);
    }
}
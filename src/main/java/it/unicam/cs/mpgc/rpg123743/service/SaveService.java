package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.repository.GameRepository;

import java.util.List;
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
     * @param repository il repository da usare per la persistenza.
     * @throws IllegalArgumentException se repository è nullo.
     */
    public SaveService(GameRepository repository) {
        if (repository == null) throw new IllegalArgumentException("GameRepository must not be null.");
        this.repository = repository;
    }

    /**
     * Salva lo stato di gioco corrente.
     *
     * @param state lo stato di gioco da persistere.
     */
    public void save(GameState state) {
        repository.save(state);
    }

    /**
     * Carica uno stato di gioco tramite il nome del salvataggio.
     *
     * @param saveName il nome del salvataggio da caricare.
     * @return un Optional contenente il GameState caricato, vuoto se non trovato.
     */
    public Optional<GameState> load(String saveName) {
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
     * @param saveName il nome del salvataggio da eliminare.
     */
    public void delete(String saveName) {
        repository.delete(saveName);
    }
}
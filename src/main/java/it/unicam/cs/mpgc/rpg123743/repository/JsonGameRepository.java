package it.unicam.cs.mpgc.rpg123743.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.unicam.cs.mpgc.rpg123743.model.Consumable;
import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.model.Item;
import it.unicam.cs.mpgc.rpg123743.model.Weapon;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementazione di IGameRepository basata su file JSON.
 * Ogni salvataggio viene memorizzato come file .json separato
 * in una cartella configurabile.
 * Utilizza Gson con RuntimeTypeAdapterFactory per serializzare e
 * deserializzare correttamente le sottoclassi polimorfiche di Item
 * (Weapon, Consumable).
 */
public class JsonGameRepository implements IGameRepository {

    private static final Logger LOGGER = Logger.getLogger(JsonGameRepository.class.getName());
    private static final String FILE_EXTENSION = ".json";

    private final Path saveDirectory;
    private final Gson gson;

    /**
     * Costruisce un nuovo repository JSON con la cartella di salvataggio specificata.
     * Se la cartella non esiste viene creata automaticamente.
     *
     * @param saveDirectory il percorso della cartella in cui salvare i file JSON.
     * @throws IllegalArgumentException se saveDirectory è nullo.
     * @throws RepositoryException      se la cartella non può essere creata.
     */
    public JsonGameRepository(Path saveDirectory) {
        if (saveDirectory == null) {
            throw new IllegalArgumentException("Save directory must not be null.");
        }
        this.saveDirectory = saveDirectory;
        this.gson = buildGson();
        ensureDirectoryExists();
    }

    /**
     * Serializza e salva lo stato di gioco su file JSON.
     *
     * @param state lo stato di gioco da salvare.
     * @throws RepositoryException se il salvataggio fallisce.
     */
    @Override
    public void save(GameState state) {
        Path filePath = resolveFilePath(state.getSaveName());
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(state, writer);
            LOGGER.info("Partita salvata: " + filePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel salvataggio: " + state.getSaveName(), e);
            throw new RepositoryException("Impossibile salvare la partita: " + state.getSaveName(), e);
        }
    }

    /**
     * Carica uno stato di gioco dal file JSON corrispondente al nome specificato.
     *
     * @param saveName il nome del salvataggio da caricare.
     * @return un Optional contenente il GameState se il file esiste, vuoto altrimenti.
     * @throws RepositoryException se il caricamento fallisce.
     */
    @Override
    public Optional<GameState> load(String saveName) {
        Path filePath = resolveFilePath(saveName);
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }
        try (Reader reader = Files.newBufferedReader(filePath)) {
            GameState state = gson.fromJson(reader, GameState.class);
            LOGGER.info("Partita caricata: " + filePath);
            return Optional.of(state);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento: " + saveName, e);
            throw new RepositoryException("Impossibile caricare la partita: " + saveName, e);
        }
    }

    /**
     * Restituisce la lista ordinata dei nomi di tutti i salvataggi disponibili.
     *
     * @return lista dei nomi dei salvataggi, vuota se nessuno è presente o in caso di errore.
     */
    @Override
    public List<String> listSaves() {
        try (Stream<Path> files = Files.list(saveDirectory)) {
            return files
                    .filter(p -> p.toString().endsWith(FILE_EXTENSION))
                    .map(p -> p.getFileName().toString()
                            .replace(FILE_EXTENSION, ""))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Errore nel recupero dei salvataggi.", e);
            return Collections.emptyList();
        }
    }

    /**
     * Elimina il file JSON corrispondente al nome del salvataggio specificato.
     *
     * @param saveName il nome del salvataggio da eliminare.
     * @throws RepositoryException se l'eliminazione fallisce.
     */
    @Override
    public void delete(String saveName) {
        Path filePath = resolveFilePath(saveName);
        try {
            Files.deleteIfExists(filePath);
            LOGGER.info("Salvataggio eliminato: " + saveName);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Errore nell'eliminazione: " + saveName, e);
            throw new RepositoryException("Impossibile eliminare il salvataggio: " + saveName, e);
        }
    }

    // --- Metodi privati di supporto ---

    /** Restituisce il percorso completo del file JSON per il nome del salvataggio dato. */
    private Path resolveFilePath(String saveName) {
        return saveDirectory.resolve(saveName + FILE_EXTENSION);
    }

    /** Crea la cartella dei salvataggi se non esiste già. */
    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(saveDirectory);
        } catch (IOException e) {
            throw new RepositoryException("Impossibile creare la cartella dei salvataggi: " + saveDirectory, e);
        }
    }

    /**
     * Costruisce un'istanza Gson con RuntimeTypeAdapterFactory per le sottoclassi di Item.
     * Questo permette la corretta serializzazione e deserializzazione di Weapon e Consumable.
     */
    private Gson buildGson() {
        RuntimeTypeAdapterFactory<Item> itemAdapter = RuntimeTypeAdapterFactory
                .of(Item.class, "itemType")
                .registerSubtype(Weapon.class, "Weapon")
                .registerSubtype(Consumable.class, "Consumable");

        return new GsonBuilder()
                .registerTypeAdapterFactory(itemAdapter)
                .setPrettyPrinting()
                .create();
    }
}
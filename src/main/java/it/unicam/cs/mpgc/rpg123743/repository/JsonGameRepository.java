package it.unicam.cs.mpgc.rpg123743.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unicam.cs.mpgc.rpg123743.model.GameState;
import it.unicam.cs.mpgc.rpg123743.model.Item;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementazione di {@link GameRepository} basata su file JSON.
 * Ogni sessione di gioco viene memorizzata come file .json separato all'interno di una cartella dedicata.
 * Utilizza Gson combinato con {@link ItemTypeAdapter} per gestire la serializzazione e
 * deserializzazione polimorfica automatica delle sottoclassi di Item (es. Weapon).
 */
public class JsonGameRepository implements GameRepository {

    private static final Logger LOGGER = Logger.getLogger(JsonGameRepository.class.getName());
    private static final String FILE_EXTENSION = ".json";

    private final Path saveDirectory;
    private final Gson gson;

    /**
     * Costruisce un nuovo repository JSON associato alla cartella di salvataggio specificata.
     * Se la cartella di destinazione non esiste, viene creata automaticamente in modo ricorsivo.
     *
     * @param saveDirectory il percorso della cartella di salvataggio (non nullo).
     * @throws NullPointerException se saveDirectory è nullo.
     * @throws RepositoryException se la cartella non esiste e non può essere creata.
     */
    public JsonGameRepository(Path saveDirectory) {
        this.saveDirectory = Objects.requireNonNull(saveDirectory, "Save directory must not be null.");
        this.gson = buildGson();
        ensureDirectoryExists();
    }

    /**
     * Serializza e salva lo stato di gioco su un file JSON dedicato.
     *
     * @param state lo stato di gioco da salvare (non nullo).
     * @throws NullPointerException se state è nullo.
     * @throws RepositoryException se la scrittura su file fallisce.
     */
    @Override
    public void save(GameState state) {
        Objects.requireNonNull(state, "Game state to save must not be null.");
        Path filePath = resolveFilePath(state.getSaveName());

        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(state, writer);
            LOGGER.info("Successfully saved game session to: " + filePath);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write save file for slot: " + state.getSaveName(), e);
            throw new RepositoryException("Could not save game slot: " + state.getSaveName(), e);
        }
    }

    /**
     * Carica uno stato di gioco recuperando il file JSON corrispondente al nome dello slot.
     *
     * @param saveName il nome del salvataggio da caricare (non nullo e non vuoto).
     * @return un Optional contenente il GameState ricostruito se presente, vuoto altrimenti.
     * @throws IllegalArgumentException se saveName è nullo, vuoto o composto da soli spazi.
     * @throws RepositoryException se la lettura o il parsing del file falliscono.
     */
    @Override
    public Optional<GameState> load(String saveName) {
        validateSaveName(saveName);
        Path filePath = resolveFilePath(saveName);

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            GameState state = gson.fromJson(reader, GameState.class);
            if (state != null) {
                state.getBattleMap().rebuildActiveUnitsRegistry();
            }
            LOGGER.info("Successfully loaded game session from: " + filePath);
            return Optional.ofNullable(state);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read save file for slot: " + saveName, e);
            throw new RepositoryException("Could not load game slot: " + saveName, e);
        }
    }

    /**
     * Restituisce la lista ordinata alfabeticamente dei nomi di tutti i salvataggi validi disponibili.
     *
     * @return lista contenente i nomi dei salvataggi (senza estensione .json), vuota se non ce ne sono.
     * @throws RepositoryException se l'accesso alla cartella dei salvataggi fallisce.
     */
    @Override
    public List<String> listSaves() {
        try (Stream<Path> files = Files.list(saveDirectory)) {
            return files
                    .filter(p -> p.toString().endsWith(FILE_EXTENSION))
                    .map(p -> p.getFileName().toString().replace(FILE_EXTENSION, ""))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to scan save directory: " + saveDirectory, e);
            throw new RepositoryException("Could not retrieve the list of available saves.", e);
        }
    }

    /**
     * Elimina il file JSON corrispondente al nome del salvataggio specificato.
     *
     * @param saveName il nome del salvataggio da rimuovere (non nullo e non vuoto).
     * @throws IllegalArgumentException se saveName è nullo, vuoto o composto da soli spazi.
     * @throws RepositoryException se l'eliminazione fisica del file fallisce.
     */
    @Override
    public void delete(String saveName) {
        validateSaveName(saveName);
        Path filePath = resolveFilePath(saveName);

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                LOGGER.info("Successfully deleted save file: " + filePath);
            } else {
                LOGGER.log(Level.WARNING, "Attempted to delete non-existent save file: " + filePath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to delete save file: " + saveName, e);
            throw new RepositoryException("Could not delete save slot: " + saveName, e);
        }
    }

    private Path resolveFilePath(String saveName) {
        return saveDirectory.resolve(saveName + FILE_EXTENSION);
    }

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(saveDirectory);
        } catch (IOException e) {
            throw new RepositoryException("Critical error: Could not initialize save directory: " + saveDirectory, e);
        }
    }

    private void validateSaveName(String saveName) {
        Objects.requireNonNull(saveName, "Save name must not be null.");
        if (saveName.trim().isEmpty()) {
            throw new IllegalArgumentException("Save name must not be empty or blank.");
        }
    }

    /**
     * Fabbrica interna per configurare Gson.
     * Registra l'adapter polimorfico sulla gerarchia della classe base Item.
     */
    private Gson buildGson() {
        ItemTypeAdapter adapter = new ItemTypeAdapter();
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(it.unicam.cs.mpgc.rpg123743.model.Item.class, adapter)
                .registerTypeHierarchyAdapter(it.unicam.cs.mpgc.rpg123743.model.Consumable.class, adapter)
                .setPrettyPrinting()
                .create();
    }
}
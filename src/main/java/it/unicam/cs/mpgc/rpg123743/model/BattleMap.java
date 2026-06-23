package it.unicam.cs.mpgc.rpg123743.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Rappresenta la mappa di battaglia come griglia bidimensionale di celle.
 * Fornisce metodi per interrogare e modificare lo stato della griglia,
 * incluso il posizionamento delle unità e il recupero delle celle per posizione.
 * Mantiene un registro interno delle unità attive per ottimizzare le prestazioni.
 *
 * <p>Invariante: ogni {@link Unit} registrata in {@code activeUnits} è anche
 * occupante della {@link Cell} corrispondente alla sua {@link Position}.
 * L'unico punto di ingresso per modificare questa relazione deve essere
 * questa classe (tramite {@link #placeUnit(Unit)} e {@link #removeUnit(Unit)}),
 * per evitare che le due fonti di verità si desincronizzino.</p>
 *
 * <p><b>Nota sulla persistenza:</b> {@code activeUnits} è marcato {@code transient}
 * e non viene serializzato da Gson. La griglia ({@code grid}) è l'unica fonte di
 * verità persistita: dopo ogni deserializzazione è necessario invocare
 * {@link #rebuildActiveUnitsRegistry()} per ricostruire il registro a partire
 * dagli occupanti delle celle. Questo evita che Gson crei due istanze distinte
 * della stessa unità (una nella cella, una nel registro), che romperebbero
 * l'invariante di identità tra i due riferimenti dopo un round-trip JSON.</p>
 */
public class BattleMap {

    private final String name;
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    /**
     * Registro delle unità attualmente presenti sulla mappa per garantire efficienza O(1) e O(N).
     * Marcato {@code transient}: non viene serializzato, va ricostruito dopo il caricamento
     * tramite {@link #rebuildActiveUnitsRegistry()}.
     */
    private transient Set<Unit> activeUnits;

    /**
     * Costruisce una nuova mappa di battaglia con le dimensioni specificate.
     * Tutte le celle della griglia vengono inizializzate a una cella vuota
     * di default nella posizione corrispondente, per evitare riferimenti
     * {@code null} non gestiti finché il chiamante non sovrascrive le celle
     * desiderate con {@link #setCell(Cell)}.
     *
     * @param name il nome della mappa.
     * @param rows il numero di righe (deve essere positivo).
     * @param cols il numero di colonne (deve essere positivo).
     * @throws IllegalArgumentException se rows o cols non sono positivi.
     */
    public BattleMap(String name, int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive.");
        }
        this.name = name;
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        this.activeUnits = new HashSet<>();
        initializeEmptyGrid();
    }

    /**
     * Popola la griglia con celle vuote di default, una per ogni posizione,
     * per garantire che {@link #getCell(Position)} non restituisca mai
     * {@code null} su una mappa appena costruita.
     */
    private void initializeEmptyGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(new Position(r, c), TerrainType.PLAIN);
            }
        }
    }

    /**
     * Ricostruisce il registro {@code activeUnits} scansionando la griglia e
     * raccogliendo tutte le celle occupate. Deve essere invocato esplicitamente
     * dopo ogni deserializzazione (es. da {@code JsonGameRepository.load()}),
     * dato che Gson non popola i campi {@code transient}.
     *
     * <p>Se {@code activeUnits} non è ancora stato inizializzato (caso tipico
     * subito dopo una deserializzazione, dove Gson bypassa il costruttore),
     * viene creato qui.</p>
     */
    public void rebuildActiveUnitsRegistry() {
        this.activeUnits = new HashSet<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (cell != null && cell.isOccupied()) {
                    activeUnits.add(cell.getOccupant());
                }
            }
        }
    }

    /**
     * Inserisce una cella nella griglia alla sua posizione, sovrascrivendo
     * quella eventualmente presente.
     *
     * @param cell la cella da inserire.
     * @throws IllegalArgumentException se la posizione della cella è fuori dai limiti.
     */
    public void setCell(Cell cell) {
        Position pos = cell.getPosition();
        if (!isInBounds(pos)) {
            throw new IllegalArgumentException("Position " + pos + " is out of bounds.");
        }
        grid[pos.getRow()][pos.getCol()] = cell;
    }

    /**
     * Restituisce la cella alla posizione specificata. Non restituisce mai
     * {@code null}: ogni posizione valida contiene sempre una cella, almeno
     * quella vuota di default creata dal costruttore.
     *
     * @param position la posizione da recuperare.
     * @return la cella in quella posizione.
     * @throws IllegalArgumentException se la posizione è fuori dai limiti.
     */
    public Cell getCell(Position position) {
        if (!isInBounds(position)) {
            throw new IllegalArgumentException("Position " + position + " is out of bounds.");
        }
        return grid[position.getRow()][position.getCol()];
    }

    /**
     * Posiziona un'unità sulla mappa nella sua posizione corrente e la registra
     * internamente. Questo è l'unico metodo che deve essere usato per collegare
     * un'unità a una cella: mantiene sincronizzati il registro {@code activeUnits}
     * e l'occupante della cella corrispondente.
     *
     * @param unit l'unità da posizionare.
     */
    public void placeUnit(Unit unit) {
        Cell cell = getCell(unit.getPosition());
        cell.setOccupant(unit);
        activeUnits.add(unit);
    }

    /**
     * Rimuove un'unità dalla mappa (ad esempio quando viene sconfitta) e dal
     * registro interno, mantenendo coerente lo stato tra cella e registro.
     *
     * @param unit l'unità da rimuovere.
     */
    public void removeUnit(Unit unit) {
        getCell(unit.getPosition()).clearOccupant();
        activeUnits.remove(unit);
    }

    /**
     * Sposta un'unità già presente sulla mappa dalla sua posizione corrente
     * a una nuova posizione, aggiornando in modo atomico cella di origine,
     * cella di destinazione, registro interno e posizione dell'unità stessa.
     *
     * @param unit        l'unità da spostare, già presente sulla mappa.
     * @param newPosition la posizione di destinazione.
     * @throws IllegalArgumentException se la posizione di destinazione è fuori dai limiti,
     *                                   non è attraversabile (es. muro), o è già occupata
     *                                   da un'altra unità.
     */
    public void moveUnit(Unit unit, Position newPosition) {
        if (!isInBounds(newPosition)) {
            throw new IllegalArgumentException("Position " + newPosition + " is out of bounds.");
        }
        Cell destination = getCell(newPosition);
        if (!destination.isPassable()) {
            throw new IllegalArgumentException("Position " + newPosition + " is not passable.");
        }
        if (destination.isOccupied()) {
            throw new IllegalArgumentException("Position " + newPosition + " is already occupied.");
        }
        getCell(unit.getPosition()).clearOccupant();
        unit.setPosition(newPosition);
        destination.setOccupant(unit);
    }

    /**
     * Restituisce tutte le unità presenti sulla mappa appartenenti alla fazione specificata.
     * Operazione ottimizzata che non richiede la scansione dell'intera griglia.
     * La lista restituita è immutabile: eventuali modifiche allo stato della mappa
     * vanno effettuate esclusivamente tramite i metodi di {@code BattleMap}.
     *
     * @param faction la fazione di cui recuperare le unità.
     * @return lista immutabile di unità della fazione, possibilmente vuota.
     */
    public List<Unit> getUnitsByFaction(Faction faction) {
        return activeUnits.stream()
                .filter(unit -> unit.getFaction() == faction)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    /**
     * Restituisce le celle adiacenti alla posizione data in base ai delta direzionali forniti.
     * Questo metodo rispetta il principio Open/Closed (OCP), permettendo di estendere le regole
     * di adiacenza (es. diagonali, esagonali) senza modificare la logica interna.
     *
     * @param position   la posizione di riferimento.
     * @param directions l'array dei delta delle coordinate (es. {{-1, 0}, {1, 0}})
     * @return lista di celle adiacenti valide secondo le direzioni fornite.
     */
    public List<Cell> getAdjacentCells(Position position, int[][] directions) {
        List<Cell> adjacent = new ArrayList<>();
        for (int[] delta : directions) {
            int newRow = position.getRow() + delta[0];
            int newCol = position.getCol() + delta[1];
            if (newRow < 0 || newCol < 0) {
                continue; // Position non accetta indici negativi: skippiamo senza lanciare eccezioni.
            }
            Position nextPos = new Position(newRow, newCol);
            if (isInBounds(nextPos)) {
                adjacent.add(grid[newRow][newCol]);
            }
        }
        return adjacent;
    }

    /**
     * Restituisce le celle adiacenti alla posizione data nelle quattro direzioni cardinali,
     * escludendo le posizioni fuori dai limiti della mappa (comportamento predefinito).
     *
     * @param position la posizione di riferimento.
     * @return lista di celle adiacenti valide nelle direzioni cardinali.
     */
    public List<Cell> getAdjacentCells(Position position) {
        int[][] cardinalDirections = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        return getAdjacentCells(position, cardinalDirections);
    }

    /**
     * Restituisce {@code true} se la posizione specificata è all'interno dei limiti della mappa.
     *
     * @param position la posizione da verificare.
     * @return {@code true} se la posizione è valida.
     */
    public boolean isInBounds(Position position) {
        return position.getRow() >= 0 && position.getRow() < rows
                && position.getCol() >= 0 && position.getCol() < cols;
    }

    /**
     * Restituisce il nome della mappa.
     *
     * @return il nome della mappa.
     */
    public String getName() {
        return name;
    }

    /**
     * Restituisce il numero di righe della griglia.
     *
     * @return il numero di righe.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Restituisce il numero di colonne della griglia.
     *
     * @return il numero di colonne.
     */
    public int getCols() {
        return cols;
    }
}
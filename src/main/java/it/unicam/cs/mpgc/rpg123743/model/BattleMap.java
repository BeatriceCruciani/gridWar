package it.unicam.cs.mpgc.rpg123743.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la mappa di battaglia come griglia bidimensionale di celle.
 * Fornisce metodi per interrogare e modificare lo stato della griglia,
 * incluso il posizionamento delle unità e il recupero delle celle per posizione.
 */
public class BattleMap {

    private final String name;
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    /**
     * Costruisce una nuova mappa di battaglia con le dimensioni specificate.
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
    }

    /**
     * Inserisce una cella nella griglia alla sua posizione.
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
     * Restituisce la cella alla posizione specificata.
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
     * Sposta un'unità dalla sua posizione corrente a una destinazione sulla mappa.
     *
     * @param unit        l'unità da spostare.
     * @param destination la posizione di destinazione.
     * @throws IllegalStateException se la cella di destinazione non è disponibile.
     */
    public void moveUnit(Unit unit, Position destination) {
        Cell origin = getCell(unit.getPosition());
        Cell dest = getCell(destination);
        if (!dest.isAvailable()) {
            throw new IllegalStateException("Destination " + destination + " is not available.");
        }
        origin.clearOccupant();
        dest.setOccupant(unit);
        unit.setPosition(destination);
    }

    /**
     * Posiziona un'unità sulla mappa nella sua posizione corrente.
     *
     * @param unit l'unità da posizionare.
     */
    public void placeUnit(Unit unit) {
        getCell(unit.getPosition()).setOccupant(unit);
    }

    /**
     * Rimuove un'unità dalla mappa (ad esempio quando viene sconfitta).
     *
     * @param unit l'unità da rimuovere.
     */
    public void removeUnit(Unit unit) {
        getCell(unit.getPosition()).clearOccupant();
    }

    /**
     * Restituisce tutte le unità presenti sulla mappa appartenenti alla fazione specificata.
     *
     * @param faction la fazione di cui recuperare le unità.
     * @return lista di unità della fazione, possibilmente vuota.
     */
    public List<Unit> getUnitsByFaction(Faction faction) {
        List<Unit> units = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = grid[r][c];
                if (cell != null && cell.isOccupied() && cell.getOccupant().getFaction() == faction) {
                    units.add(cell.getOccupant());
                }
            }
        }
        return units;
    }

    /**
     * Restituisce le celle adiacenti alla posizione data nelle quattro direzioni cardinali,
     * escludendo le posizioni fuori dai limiti della mappa.
     *
     * @param position la posizione di riferimento.
     * @return lista di celle adiacenti valide.
     */
    public List<Cell> getAdjacentCells(Position position) {
        List<Cell> adjacent = new ArrayList<>();
        int[][] deltas = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] delta : deltas) {
            int newRow = position.getRow() + delta[0];
            int newCol = position.getCol() + delta[1];
            Position neighbour = new Position(newRow, newCol);
            if (isInBounds(neighbour)) {
                adjacent.add(grid[newRow][newCol]);
            }
        }
        return adjacent;
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

    /** Restituisce il nome della mappa. */
    public String getName() { return name; }
    /** Restituisce il numero di righe della griglia. */
    public int getRows() { return rows; }
    /** Restituisce il numero di colonne della griglia. */
    public int getCols() { return cols; }
}
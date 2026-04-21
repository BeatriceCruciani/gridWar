package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta una posizione immutabile sulla griglia della mappa di battaglia.
 * Una posizione è definita da un indice di riga e uno di colonna, entrambi a base zero.
 */
public final class Position {

    private final int row;
    private final int col;

    /**
     * Crea una nuova posizione con la riga e la colonna specificate.
     *
     * @param row indice di riga (non negativo).
     * @param col indice di colonna (non negativo).
     * @throws IllegalArgumentException se row o col sono negativi.
     */
    public Position(int row, int col) {
        if (row < 0 || col < 0) {
            throw new IllegalArgumentException("Row and column must be non-negative.");
        }
        this.row = row;
        this.col = col;
    }

    /** Restituisce l'indice di riga. */
    public int getRow() {
        return row;
    }

    /** Restituisce l'indice di colonna. */
    public int getCol() {
        return col;
    }

    /**
     * Calcola la distanza di Manhattan tra questa posizione e un'altra.
     * Utilizzata per calcolare il raggio di movimento e di attacco delle unità.
     *
     * @param other la posizione di destinazione.
     * @return la distanza di Manhattan tra le due posizioni.
     */
    public int distanceTo(Position other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position other)) return false;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
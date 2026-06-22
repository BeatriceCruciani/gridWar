package it.unicam.cs.mpgc.rpg123743.model;

import java.util.Objects;

/**
 * Rappresenta una posizione immutabile sulla griglia della mappa di battaglia di GridWar.
 * Una posizione è definita da un indice di riga e uno di colonna, entrambi a base zero.
 * Essendo un Value Object immutabile, è intrinsecamente thread-safe e sicura da condividere.
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
            throw new IllegalArgumentException("Row and column must be non-negative. Got: (" + row + ", " + col + ")");
        }
        this.row = row;
        this.col = col;
    }

    /** @return l'indice di riga. */
    public int getRow() {
        return row;
    }

    /** @return l'indice di colonna. */
    public int getCol() {
        return col;
    }

    /**
     * Calcola la distanza di Manhattan tra questa posizione e un'altra.
     * Utilizzata per determinare il raggio di movimento e la gittata di attacco/cura.
     *
     * @param other la posizione di destinazione (non nulla).
     * @return la distanza di Manhattan (numero di celle) tra le due posizioni.
     * @throws NullPointerException se other è nullo.
     */
    public int distanceTo(Position other) {
        Objects.requireNonNull(other, "Target position must not be null.");
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
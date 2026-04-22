package it.unicam.cs.mpgc.rpg123743.repository;

/**
 * Eccezione unchecked lanciata quando un'operazione sul repository fallisce.
 * Incapsula le eccezioni di I/O di basso livello per disaccoppiare il service layer
 * dai dettagli implementativi dello storage.
 */
public class RepositoryException extends RuntimeException {

    /**
     * Costruisce una nuova eccezione con messaggio e causa specificati.
     *
     * @param message il messaggio descrittivo dell'errore.
     * @param cause   la causa originale dell'eccezione.
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Costruisce una nuova eccezione con solo il messaggio specificato.
     *
     * @param message il messaggio descrittivo dell'errore.
     */
    public RepositoryException(String message) {
        super(message);
    }
}

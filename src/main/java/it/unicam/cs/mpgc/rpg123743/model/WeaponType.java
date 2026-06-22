package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Rappresenta il tipo di arma che un'unità può impugnare in GridWar.
 * Gestisce nativamente le interazioni del triangolo delle armi e i tipi di utility (Staff).
 */
public enum WeaponType {
    /** Spada — batte l'ascia nel triangolo delle armi. */
    SWORD,
    /** Ascia — batte la lancia nel triangolo delle armi. */
    AXE,
    /** Lancia — batte la spada nel triangolo delle armi. */
    LANCE,
    /** Arco — neutro, attacca a distanza. */
    BOW,
    /** Magia — neutro, usa la resistenza del bersaglio. */
    MAGIC,
    /** Bastone — neutro, utilizzato dagli Healer per curare gli alleati. Non infligge danni. */
    STAFF;

    /**
     * Rappresenta il tipo di relazione nel triangolo, associando a ciascuna
     * il rispettivo moltiplicatore di danno.
     */
    public enum TriangleRelation {
        ADVANTAGE(1.2),
        DISADVANTAGE(0.8),
        NEUTRAL(1.0);

        private final double multiplier;

        TriangleRelation(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }

    /**
     * Determina la relazione di vantaggio/svantaggio di questa arma contro un'arma avversaria.
     * Se l'avversario non ha un'arma equipaggiata, lo scontro è considerato neutrale.
     */
    public TriangleRelation getRelationAgainst(WeaponType opponent) {
        // FIX: Se l'avversario è disarmato (null), la relazione è neutrale senza mandare in crash il gioco
        if (opponent == null) {
            return TriangleRelation.NEUTRAL;
        }

        return switch (this) {
            case SWORD -> switch (opponent) {
                case AXE -> TriangleRelation.ADVANTAGE;
                case LANCE -> TriangleRelation.DISADVANTAGE;
                default -> TriangleRelation.NEUTRAL;
            };
            case AXE -> switch (opponent) {
                case LANCE -> TriangleRelation.ADVANTAGE;
                case SWORD -> TriangleRelation.DISADVANTAGE;
                default -> TriangleRelation.NEUTRAL;
            };
            case LANCE -> switch (opponent) {
                case SWORD -> TriangleRelation.ADVANTAGE;
                case AXE -> TriangleRelation.DISADVANTAGE;
                default -> TriangleRelation.NEUTRAL;
            };
            // Arco, Magia e Bastone sono neutrali a prescindere
            case BOW, MAGIC, STAFF -> TriangleRelation.NEUTRAL;
        };
    }
}
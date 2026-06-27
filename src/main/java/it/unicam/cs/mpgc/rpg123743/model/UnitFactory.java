package it.unicam.cs.mpgc.rpg123743.model;

/**
 * Costruisce istanze di {@link Unit} completamente equipaggiate, una per ogni
 * classe disponibile nel gioco. Ogni unità riceve l'arma appropriata alla
 * propria classe e una pozione curativa di riserva.
 * Questa classe è una utility class: non può essere istanziata.
 */
public final class UnitFactory {

    private static final String POTION_NAME        = "Vulnerary";
    private static final String POTION_DESCRIPTION = "Restores 10 HP.";
    private static final int    POTION_CHARGES     = 3;
    private static final int    POTION_HEAL_AMOUNT = 10;

    private UnitFactory() {}

    /**
     * Crea un'unità Warrior equipaggiata con una spada di ferro.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit warrior(String name, Faction faction, Position pos) {
        Stats stats = new Stats(30, 12, 8, 3, 6, 3);
        Weapon weapon = new Weapon("Iron Sword", "A sturdy iron sword.", 50, WeaponType.SWORD, 5, 1);
        return buildUnit(name, faction, UnitClass.WARRIOR, stats, pos, weapon);
    }

    /**
     * Crea un'unità Mage equipaggiata con un tomo del fuoco.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit mage(String name, Faction faction, Position pos) {
        Stats stats = new Stats(20, 14, 3, 8, 8, 4);
        Weapon weapon = new Weapon("Fire Tome", "A basic fire tome.", 25, WeaponType.MAGIC, 6, 2);
        return buildUnit(name, faction, UnitClass.MAGE, stats, pos, weapon);
    }

    /**
     * Crea un'unità Archer equipaggiata con un arco di ferro.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit archer(String name, Faction faction, Position pos) {
        Stats stats = new Stats(22, 11, 5, 4, 9, 4);
        Weapon weapon = new Weapon("Iron Bow", "A reliable bow.", 50, WeaponType.BOW, 5, 2);
        return buildUnit(name, faction, UnitClass.ARCHER, stats, pos, weapon);
    }

    /**
     * Crea un'unità Knight equipaggiata con una lancia di ferro.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit knight(String name, Faction faction, Position pos) {
        Stats stats = new Stats(28, 10, 12, 4, 5, 5);
        Weapon weapon = new Weapon("Iron Lance", "A heavy iron lance.", 50, WeaponType.LANCE, 5, 1);
        return buildUnit(name, faction, UnitClass.KNIGHT, stats, pos, weapon);
    }

    /**
     * Crea un'unità Thief equipaggiata con un pugnale.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit thief(String name, Faction faction, Position pos) {
        Stats stats = new Stats(18, 9, 4, 4, 13, 6);
        Weapon weapon = new Weapon("Dagger", "A swift dagger.", 45, WeaponType.SWORD, 3, 1);
        return buildUnit(name, faction, UnitClass.THIEF, stats, pos, weapon);
    }

    /**
     * Crea un'unità Healer equipaggiata con un bastone curativo.
     * Il bastone è di tipo {@link WeaponType#STAFF}: {@code BattleController}
     * lo interpreta come azione di supporto (cura di un alleato) anziché come attacco.
     *
     * @param name    il nome dell'unità.
     * @param faction la fazione di appartenenza.
     * @param pos     la posizione iniziale sulla griglia.
     * @return l'unità pronta all'uso.
     */
    public static Unit healer(String name, Faction faction, Position pos) {
        Stats stats = new Stats(20, 8, 2, 9, 7, 5);
        Weapon weapon = new Weapon("Healing Staff", "A staff imbued with restorative magic.", 30, WeaponType.STAFF, 8, 2);
        return buildUnit(name, faction, UnitClass.HEALER, stats, pos, weapon);
    }

    /**
     * Costruisce un'unità completa: la crea con le statistiche indicate,
     * le equipaggia con l'arma fornita e aggiunge una pozione curativa
     * di riserva. Tutti i metodi pubblici di questa classe delegano qui.
     *
     * @param name      il nome dell'unità.
     * @param faction   la fazione di appartenenza.
     * @param unitClass la classe dell'unità.
     * @param stats     le statistiche di base.
     * @param pos       la posizione iniziale sulla griglia.
     * @param weapon    l'arma con cui equipaggiare l'unità.
     * @return l'unità pronta, con arma equipaggiata e una pozione in inventario.
     */
    private static Unit buildUnit(String name, Faction faction, UnitClass unitClass,
                                  Stats stats, Position pos, Weapon weapon) {
        Unit unit = new Unit(name, faction, unitClass, stats, pos);
        unit.addItem(weapon);
        unit.equipWeapon(weapon);
        unit.addItem(new HealingPotion(POTION_NAME, POTION_DESCRIPTION,
                POTION_CHARGES, POTION_HEAL_AMOUNT));
        return unit;
    }
}
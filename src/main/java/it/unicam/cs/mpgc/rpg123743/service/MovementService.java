package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;
import java.util.*;

/**
 * Calcola le traiettorie di movimento e la gittata di attacco delle unità sulla mappa.
 * Implementa l'algoritmo di Dijkstra (Priority-BFS) per gestire accuratamente i costi
 * variabili dei terreni e le restrizioni di fazione.
 */
public class MovementService {

    /**
     * Costruttore stateless autonomo.
     */
    public MovementService() { }

    /**
     * Sposta un'unità in una nuova posizione sulla mappa previa verifica della sua raggiungibilità.
     * Sfrutta atomicamente i metodi placeUnit e removeUnit della BattleMap senza richiedere
     * metodi di spostamento dedicati nella mappa.
     *
     * @param unit   l'unità da muovere.
     * @param target la posizione di destinazione.
     * @param map    la mappa di gioco.
     * @throws IllegalArgumentException se la destinazione non è tra le celle raggiungibili.
     */
    public void move(Unit unit, Position target, BattleMap map) {
        Objects.requireNonNull(unit, "Unit cannot be null.");
        Objects.requireNonNull(target, "Target position cannot be null.");
        Objects.requireNonNull(map, "BattleMap cannot be null.");

        Set<Position> allowed = getReachableCells(unit, map);

        if (!target.equals(unit.getPosition()) && !allowed.contains(target)) {
            throw new IllegalArgumentException("Movimento illegale: la posizione " + target + " è fuori dalla portata dell'unità.");
        }

        map.removeUnit(unit);       // 1. Libera la vecchia cella (usa la posizione attuale dell'unità)
        unit.setPosition(target);   // 2. Aggiorna la coordinata interna dell'unità con la nuova destinazione
        map.placeUnit(unit);        // 3. Occupa la nuova cella (usa la nuova posizione appena impostata)
    }

    /**
     * Restituisce l'insieme delle posizioni raggiungibili dall'unità in un turno,
     * in base alla sua statistica di movimento e ai costi del terreno.
     *
     * <p>Le celle occupate da alleati non sono incluse nel risultato (non ci si può
     * sostare sopra), ma il percorso può comunque attraversarle per raggiungere celle
     * successive: solo le unità nemiche bloccano completamente il passaggio (Zone of
     * Control). Questa è una scelta di design intenzionale, coerente con molti giochi
     * tattici a turni dello stesso genere.</p>
     */
    public Set<Position> getReachableCells(Unit unit, BattleMap map) {
        Objects.requireNonNull(unit, "Unit cannot be null.");
        Objects.requireNonNull(map, "BattleMap cannot be null.");

        Set<Position> reachable = new HashSet<>();
        Map<Position, Integer> costSoFar = new HashMap<>();

        int maxMovement = unit.getStats().getMovement();
        Position start  = unit.getPosition();

        // Ottimizzazione: Usiamo una PriorityQueue per implementare Dijkstra.
        // Ordina i nodi in base al costo accumulato minore.
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));

        queue.add(new Node(start, 0));
        costSoFar.put(start, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Se abbiamo già trovato un percorso migliore per questa posizione, salta
            if (current.cost > costSoFar.getOrDefault(current.pos, Integer.MAX_VALUE)) {
                continue;
            }

            for (Cell neighbour : map.getAdjacentCells(current.pos)) {
                Position neighbourPos = neighbour.getPosition();

                // 1. Il terreno è strutturalmente invalicabile (es. Muri)?
                if (!neighbour.isPassable()) continue;

                // 2. C'è un nemico che blocca il passaggio? (ZOC - Zone of Control)
                if (neighbour.isOccupied() && neighbour.getOccupant().isEnemy(unit)) continue;

                // 3. Calcolo del costo energetico del passo
                int newCost = current.cost + neighbour.getTerrainType().getMovementCost();
                if (newCost > maxMovement) continue;

                // 4. Se è la strada più economica trovata finora, la registriamo
                if (newCost < costSoFar.getOrDefault(neighbourPos, Integer.MAX_VALUE)) {
                    costSoFar.put(neighbourPos, newCost);
                    queue.add(new Node(neighbourPos, newCost));

                    // Si può sostare sulla cella solo se non è occupata da nessuno (né amici né nemici)
                    if (!neighbour.isOccupied()) {
                        reachable.add(neighbourPos);
                    }
                }
            }
        }

        return reachable;
    }

    /**
     * Restituisce l'insieme delle posizioni che l'unità può colpire geometricamente
     * dalla sua posizione attuale in base alla gittata della sua arma.
     */
    public Set<Position> getAttackRange(Unit unit, BattleMap map) {
        Objects.requireNonNull(unit, "Unit cannot be null.");
        Objects.requireNonNull(map, "BattleMap cannot be null.");

        Set<Position> attackRange = new HashSet<>();
        Optional<Weapon> weapon = unit.getEquippedWeapon();
        if (weapon.isEmpty()) return attackRange;

        int range       = weapon.get().getRange();
        Position origin = unit.getPosition();

        // Ottimizzazione: Scansioniamo solo l'area a "rombo" attorno all'unità anziché tutta la mappa
        for (int r = origin.getRow() - range; r <= origin.getRow() + range; r++) {
            for (int c = origin.getCol() - range; c <= origin.getCol() + range; c++) {

                // Controllo dei confini della mappa
                if (r < 0 || r >= map.getRows() || c < 0 || c >= map.getCols()) continue;

                Position pos = new Position(r, c);

                // Un'unità non può auto-bersagliarsi con un attacco standard
                if (pos.equals(origin)) continue;

                if (origin.distanceTo(pos) <= range) {
                    Cell cell = map.getCell(pos);
                    // I muri (permanenti o distruttibili ancora intatti) bloccano la linea di tiro
                    if (cell.getTerrainType() != TerrainType.WALL && !cell.isBreakableWall()) {
                        attackRange.add(pos);
                    }
                }
            }
        }

        return attackRange;
    }

    /**
     * Record di supporto interno per la PriorityQueue dell'algoritmo di Dijkstra.
     */
    private static class Node {
        final Position pos;
        final int cost;

        Node(Position pos, int cost) {
            this.pos = pos;
            this.cost = cost;
        }
    }
}
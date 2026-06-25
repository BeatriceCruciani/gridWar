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
            throw new IllegalArgumentException("Illegal move: position " + target + " is out of range.");
        }

        map.removeUnit(unit);
        unit.setPosition(target);
        map.placeUnit(unit);
    }

    /**
     * Restituisce l'insieme delle posizioni raggiungibili dall'unità in un turno.
     * Le celle occupate da alleati non sono incluse nel risultato, ma il percorso
     * può attraversarle. Solo le unità nemiche bloccano il passaggio.
     *
     * @param unit l'unità selezionata.
     * @param map  la mappa di gioco.
     * @return un insieme di posizioni valide.
     */
    public Set<Position> getReachableCells(Unit unit, BattleMap map) {
        Objects.requireNonNull(unit, "Unit cannot be null.");
        Objects.requireNonNull(map, "BattleMap cannot be null.");

        Set<Position> reachable = new HashSet<>();
        Map<Position, Integer> costSoFar = new HashMap<>();

        int maxMovement = unit.getStats().getMovement();
        Position start  = unit.getPosition();

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));

        queue.add(new Node(start, 0));
        costSoFar.put(start, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.cost > costSoFar.getOrDefault(current.pos, Integer.MAX_VALUE)) {
                continue;
            }

            for (Cell neighbour : map.getAdjacentCells(current.pos)) {
                Position neighbourPos = neighbour.getPosition();

                if (!neighbour.isPassable()) continue;
                if (neighbour.isOccupied() && neighbour.getOccupant().isEnemy(unit)) continue;

                int newCost = current.cost + neighbour.getTerrainType().getMovementCost();
                if (newCost > maxMovement) continue;

                if (newCost < costSoFar.getOrDefault(neighbourPos, Integer.MAX_VALUE)) {
                    costSoFar.put(neighbourPos, newCost);
                    queue.add(new Node(neighbourPos, newCost));

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

        int range = weapon.get().getRange();
        Position origin = unit.getPosition();

        for (int r = origin.getRow() - range; r <= origin.getRow() + range; r++) {
            for (int c = origin.getCol() - range; c <= origin.getCol() + range; c++) {
                if (r < 0 || r >= map.getRows() || c < 0 || c >= map.getCols()) continue;

                Position pos = new Position(r, c);
                if (pos.equals(origin)) continue;

                if (origin.distanceTo(pos) <= range) {
                    Cell cell = map.getCell(pos);
                    if (cell.getTerrainType() != TerrainType.WALL) {
                        attackRange.add(pos);
                    }
                }
            }
        }
        return attackRange;
    }

    private static class Node {
        final Position pos;
        final int cost;
        Node(Position pos, int cost) { this.pos = pos; this.cost = cost; }
    }
}
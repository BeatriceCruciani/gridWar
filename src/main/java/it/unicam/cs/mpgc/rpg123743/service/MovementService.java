package it.unicam.cs.mpgc.rpg123743.service;

import it.unicam.cs.mpgc.rpg123743.model.*;

import java.util.*;

/**
 * Calcola le celle raggiungibili da un'unità sulla mappa di battaglia.
 * Utilizza un algoritmo BFS (Breadth-First Search) che rispetta
 * i costi di movimento del terreno.
 * Le celle occupate da nemici bloccano il movimento; quelle occupate
 * da alleati vengono saltate ma non bloccano i percorsi attraverso di esse.
 */
public class MovementService {

    /**
     * Restituisce l'insieme delle posizioni raggiungibili dall'unità in un turno,
     * in base alla sua statistica di movimento e ai costi del terreno sulla mappa.
     * La posizione corrente dell'unità è esclusa dal risultato.
     *
     * @param unit l'unità di cui calcolare il raggio di movimento.
     * @param map  la mappa di battaglia.
     * @return insieme delle posizioni raggiungibili dall'unità.
     */
    public Set<Position> getReachableCells(Unit unit, BattleMap map) {
        Set<Position> reachable   = new HashSet<>();
        Map<Position, Integer> costSoFar = new HashMap<>();

        int maxMovement = unit.getStats().getMovement();
        Position start  = unit.getPosition();

        Queue<Position> queue = new LinkedList<>();
        queue.add(start);
        costSoFar.put(start, 0);

        while (!queue.isEmpty()) {
            Position current     = queue.poll();
            int currentCost      = costSoFar.get(current);

            for (Cell neighbour : map.getAdjacentCells(current)) {
                Position neighbourPos = neighbour.getPosition();
                TerrainType terrain   = neighbour.getTerrainType();

                if (!neighbour.isPassable()) continue;

                if (neighbour.isOccupied() && neighbour.getOccupant().isEnemy(unit)) continue;

                int newCost = currentCost + terrain.getMovementCost();
                if (newCost > maxMovement) continue;

                if (!costSoFar.containsKey(neighbourPos) || newCost < costSoFar.get(neighbourPos)) {
                    costSoFar.put(neighbourPos, newCost);
                    queue.add(neighbourPos);

                    if (!neighbour.isOccupied()) {
                        reachable.add(neighbourPos);
                    }
                }
            }
        }

        return reachable;
    }

    /**
     * Restituisce l'insieme delle posizioni che l'unità può attaccare
     * dalla sua posizione corrente, in base alla gittata dell'arma equipaggiata.
     *
     * @param unit l'unità di cui calcolare il raggio d'attacco.
     * @param map  la mappa di battaglia.
     * @return insieme delle posizioni nel raggio d'attacco.
     */
    public Set<Position> getAttackRange(Unit unit, BattleMap map) {
        Set<Position> attackRange = new HashSet<>();
        if (unit.getEquippedWeapon() == null) return attackRange;

        int range       = unit.getEquippedWeapon().getRange();
        Position origin = unit.getPosition();

        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Position pos = new Position(r, c);
                if (origin.distanceTo(pos) <= range && !pos.equals(origin)) {
                    Cell cell = map.getCell(pos);
                    if (cell.getTerrainType() != TerrainType.WALL
                            && (!cell.isOccupied() || cell.getOccupant().isEnemy(unit))) {
                        attackRange.add(pos);
                    }
                }
            }
        }

        return attackRange;
    }
}
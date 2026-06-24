package it.unicam.cs.mpgc.rpg123743.ui.javafx.view;

import it.unicam.cs.mpgc.rpg123743.model.Cell;
import it.unicam.cs.mpgc.rpg123743.model.Faction;
import it.unicam.cs.mpgc.rpg123743.model.Position;
import it.unicam.cs.mpgc.rpg123743.model.Unit;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.InputStream;

/**
 * Rappresenta il nodo grafico visivo di una singola cella all'interno della griglia di gioco.
 * Estende {@link StackPane} per stratificare il terreno, gli highlight di selezione,
 * le unità e le informazioni strutturali (es. muri distruggibili).
 */
public class MapCellNode extends StackPane {

    private static final int CELL_SIZE = 64;

    /**
     * Costruisce il nodo grafico per una specifica cella calcolando i relativi strati visivi.
     *
     * @param cell         L'oggetto {@link Cell} del modello da rappresentare.
     * @param pos          La {@link Position} logica della cella all'interno della mappa.
     * @param isReachable  True se la cella fa parte del raggio di movimento dell'unità selezionata.
     * @param isAttackable True se la cella fa parte del raggio di attacco dell'unità selezionata.
     * @param isSelected   True se la cella contiene l'unità attualmente selezionata dal giocatore.
     */
    public MapCellNode(Cell cell, Position pos, boolean isReachable, boolean isAttackable, boolean isSelected) {
        setPrefSize(CELL_SIZE, CELL_SIZE);
        getStyleClass().add("cell");

        // Strato 1: Il terreno di sfondo
        getChildren().add(loadTerrainTile(cell));

        // Strato 2: Gli effetti grafici di selezione o raggio d'azione
        applyHighlighting(isReachable, isAttackable, isSelected);

        // Strato 3: L'eventuale unità presente sulla cella
        if (cell.isOccupied()) {
            getChildren().add(buildUnitBox(cell.getOccupant()));
        }

        // Strato 4: L'eventuale indicatore per i muri distruggibili
        if (cell.isBreakableWall()) {
            Label wallLabel = new Label("BW\n" + cell.getWallHp());
            wallLabel.getStyleClass().add("wall-breakable");
            getChildren().add(wallLabel);
        }
    }

    /**
     * Carica l'immagine corrispondente al tipo di terreno della cella.
     * In caso di risorsa mancante, applica una classe CSS di fallback.
     *
     * @param cell La cella da cui ricavare il terreno.
     * @return Il nodo grafico {@link Node} contenente la texture o il fallback.
     */
    private Node loadTerrainTile(Cell cell) {
        String fileName = switch (cell.getTerrainType()) {
            case PLAIN          -> "plain.png";
            case FOREST         -> "forest1.png";
            case MOUNTAIN       -> "wall-fort.png";
            case FORT           -> "wall-fort-angle.png";
            case WALL           -> "wall-fort.png";
            case BREAKABLE_WALL -> "wall-fort-breakable.png";
        };

        InputStream stream = getClass().getResourceAsStream("/it/unicam/cs/mpgc/rpg123743/sprites/terrain/" + fileName);

        if (stream != null) {
            ImageView imageView = new ImageView(new Image(stream));
            imageView.setFitWidth(CELL_SIZE);
            imageView.setFitHeight(CELL_SIZE);
            return imageView;
        }

        StackPane fallback = new StackPane();
        fallback.getStyleClass().add("terrain-" + cell.getTerrainType().name().toLowerCase());
        fallback.setPrefSize(CELL_SIZE, CELL_SIZE);
        return fallback;
    }

    /**
     * Applica le classi CSS di overlay grafico in base allo stato della cella.
     */
    private void applyHighlighting(boolean isReachable, boolean isAttackable, boolean isSelected) {
        if (isReachable) {
            getStyleClass().add("cell-reachable");
        } else if (isAttackable) {
            getStyleClass().add("cell-attackable");
        }
        if (isSelected) {
            getStyleClass().add("cell-selected");
        }
    }

    /**
     * Assembla il contenitore verticale contenente lo sprite dell'unità e la sua barra della vita.
     *
     * @param unit L'unità da rappresentare graficamente.
     * @return Un contenitore {@link VBox} formattato per la griglia.
     */
    private VBox buildUnitBox(Unit unit) {
        Node graphic = loadUnitSprite(unit);

        double hpPercent = (double) unit.getStats().getCurrentHp() / unit.getStats().getMaxHp();
        ProgressBar hpBar = new ProgressBar(hpPercent);
        hpBar.setPrefWidth(CELL_SIZE - 8);
        hpBar.setPrefHeight(6);
        hpBar.getStyleClass().add("hp-bar");
        hpBar.setStyle(getHpBarColor(hpPercent));

        VBox box = new VBox(2, graphic, hpBar);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Carica lo sprite dell'unità correggendo il percorso delle risorse ed effettuando
     * il mirroring speculare per le unità della fazione del giocatore.
     */
    private Node loadUnitSprite(Unit unit) {
        String faction = unit.getFaction() == Faction.PLAYER ? "player" : "enemy";
        String fileName = switch (unit.getUnitClass()) {
            case WARRIOR -> "warrior-" + faction + ".png";
            case MAGE    -> "mage-" + faction + ".png";
            case ARCHER  -> "archer-" + faction + ".png";
            case KNIGHT  -> "knight-" + faction + ".png";
            case THIEF   -> "thief-" + faction + ".png";
            case HEALER -> "healer-" + faction + ".png";
        };

        // FIX: Sostituiti i punti originari con le barre per il package path corretto delle risorse
        InputStream stream = getClass().getResourceAsStream("/it/unicam/cs/mpgc/rpg123743/sprites/units/" + fileName);

        if (stream != null) {
            ImageView imageView = new ImageView(new Image(stream));
            imageView.setFitWidth(CELL_SIZE - 16);
            imageView.setFitHeight(CELL_SIZE - 16);
            imageView.setPreserveRatio(true);
            if (unit.getFaction() == Faction.PLAYER) {
                imageView.setScaleX(-1); // Specchia l'unità verso destra per il Player
            }
            return imageView;
        }

        Label label = new Label(getUnitSymbol(unit));
        label.getStyleClass().add(unit.getFaction() == Faction.PLAYER ? "unit-player" : "unit-enemy");
        return label;
    }

    /**
     * Restituisce la stringa di stile inline per colorare dinamicamente la barra degli HP.
     */
    private String getHpBarColor(double hpPercent) {
        if (hpPercent > 0.5)  return "-fx-accent: #44ff44;";
        if (hpPercent > 0.25) return "-fx-accent: #ffaa00;";
        return "-fx-accent: #ff4444;";
    }

    /**
     * Restituisce la sigla testuale dell'unità in caso di mancanza delle texture grafiche.
     */
    private String getUnitSymbol(Unit unit) {
        return switch (unit.getUnitClass()) {
            case WARRIOR -> "W";
            case MAGE    -> "M";
            case ARCHER  -> "A";
            case KNIGHT  -> "K";
            case THIEF   -> "T";
            case HEALER -> "H";
        };
    }
}
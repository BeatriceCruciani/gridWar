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
 * Estende {@link StackPane} per stratificare il terreno, le unità e le informazioni
 * strutturali (es. muri distruggibili).
 * A differenza di un overlay CSS, l'evidenziazione di movimento/attacco viene resa
 * sostituendo direttamente lo sprite del terreno con la sua variante colorata
 * (es. {@code plain.png} → {@code plain-blue.png} per il movimento,
 * {@code plain-red.png} per l'attacco).
 */
public class MapCellNode extends StackPane {

    private static final int CELL_SIZE      = 64;
    private static final int UNIT_SIZE      = CELL_SIZE - 16;
    private static final int HP_BAR_HEIGHT  = 6;

    private static final String TERRAIN_PATH = "/it/unicam/cs/mpgc/rpg123743/sprites/terrain/";
    private static final String UNIT_PATH    = "/it/unicam/cs/mpgc/rpg123743/sprites/units/";

    /**
     * Costruisce il nodo grafico per una specifica cella calcolando i relativi strati visivi.
     *
     * @param cell         la cella del modello da rappresentare.
     * @param pos          la posizione logica della cella all'interno della mappa.
     * @param isReachable  {@code true} se la cella è nel raggio di movimento dell'unità selezionata.
     * @param isAttackable {@code true} se la cella è nel raggio di attacco dell'unità selezionata.
     * @param isSelected   {@code true} se la cella contiene l'unità attualmente selezionata.
     */
    public MapCellNode(Cell cell, Position pos, boolean isReachable, boolean isAttackable, boolean isSelected) {
        setPrefSize(CELL_SIZE, CELL_SIZE);
        getStyleClass().add("cell");
        getChildren().add(loadTerrainTile(cell, isReachable, isAttackable));
        if (isSelected) {
            getStyleClass().add("cell-selected");
        }
        if (cell.isOccupied()) {
            getChildren().add(buildUnitBox(cell.getOccupant()));
        }
        if (cell.isBreakableWall()) {
            Label wallLabel = new Label("BW\n" + cell.getWallHp());
            wallLabel.getStyleClass().add("wall-breakable");
            getChildren().add(wallLabel);
        }
    }

    /**
     * Carica la texture del terreno scegliendo la variante colorata se la cella
     * è raggiungibile (blu) o attaccabile (rossa). In caso di risorsa mancante
     * esegue il fallback sulla texture base e, in ultima istanza, su uno stile CSS.
     *
     * @param cell         la cella da cui ricavare il tipo di terreno.
     * @param isReachable  {@code true} se la cella è nel raggio di movimento.
     * @param isAttackable {@code true} se la cella è nel raggio di attacco.
     * @return il nodo grafico contenente la texture o il fallback CSS.
     */
    private Node loadTerrainTile(Cell cell, boolean isReachable, boolean isAttackable) {
        String baseName = switch (cell.getTerrainType()) {
            case PLAIN          -> "plain";
            case FOREST         -> "forest";
            case MOUNTAIN       -> "mountain";
            case WALL           -> "wall-fort";
            case BREAKABLE_WALL -> "wall-fort-breakable";
        };
        String suffix   = isReachable ? "-blue" : isAttackable ? "-red" : "";
        String fileName = baseName + suffix + ".png";

        InputStream stream = getResourceStream(TERRAIN_PATH + fileName);
        if (stream == null && !suffix.isEmpty()) {
            stream = getResourceStream(TERRAIN_PATH + baseName + ".png");
        }

        if (stream != null) {
            return buildImageView(stream, CELL_SIZE, CELL_SIZE, false);
        }

        String cssClass = "terrain-" + cell.getTerrainType().name().toLowerCase() + suffix;
        return buildCssPane(cssClass);
    }

    /**
     * Assembla il contenitore verticale con lo sprite dell'unità e la sua barra della vita.
     *
     * @param unit l'unità da rappresentare graficamente.
     * @return un {@link VBox} formattato per la griglia.
     */
    private VBox buildUnitBox(Unit unit) {
        Node graphic = loadUnitSprite(unit);

        double hpPercent = (double) unit.getStats().getCurrentHp() / unit.getStats().getMaxHp();
        ProgressBar hpBar = new ProgressBar(hpPercent);
        hpBar.setPrefWidth(CELL_SIZE - 8);
        hpBar.setPrefHeight(HP_BAR_HEIGHT);
        hpBar.getStyleClass().add("hp-bar");
        hpBar.setStyle(getHpBarColor(hpPercent));

        VBox box = new VBox(2, graphic, hpBar);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Carica lo sprite dell'unità applicando il mirroring speculare per le unità
     * del giocatore. In caso di risorsa mancante restituisce un fallback testuale.
     *
     * @param unit l'unità di cui caricare lo sprite.
     * @return il nodo grafico contenente lo sprite o il fallback testuale.
     */
    private Node loadUnitSprite(Unit unit) {
        String faction  = unit.getFaction() == Faction.PLAYER ? "player" : "enemy";
        String fileName = switch (unit.getUnitClass()) {
            case WARRIOR -> "warrior-" + faction + ".png";
            case MAGE    -> "mage-"    + faction + ".png";
            case ARCHER  -> "archer-"  + faction + ".png";
            case KNIGHT  -> "knight-"  + faction + ".png";
            case THIEF   -> "thief-"   + faction + ".png";
            case HEALER  -> "healer-"  + faction + ".png";
        };

        InputStream stream = getResourceStream(UNIT_PATH + fileName);
        if (stream != null) {
            boolean mirror = unit.getFaction() == Faction.PLAYER;
            return buildImageView(stream, UNIT_SIZE, UNIT_SIZE, mirror);
        }

        Label label = new Label(getUnitSymbol(unit));
        label.getStyleClass().add(unit.getFaction() == Faction.PLAYER ? "unit-player" : "unit-enemy");
        return label;
    }

    /**
     * Tenta di aprire uno stream per la risorsa al percorso indicato.
     *
     * @param path il percorso assoluto della risorsa nel classpath.
     * @return lo stream aperto, o {@code null} se la risorsa non esiste.
     */
    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    /**
     * Crea un {@link ImageView} dallo stream fornito con le dimensioni specificate.
     *
     * @param stream   lo stream dell'immagine da caricare.
     * @param width    la larghezza in pixel.
     * @param height   l'altezza in pixel.
     * @param mirrorX  se {@code true}, applica il mirroring orizzontale (scaleX = -1).
     * @return l'imageview configurato.
     */
    private ImageView buildImageView(InputStream stream, int width, int height, boolean mirrorX) {
        ImageView imageView = new ImageView(new Image(stream));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        if (mirrorX) imageView.setScaleX(-1);
        return imageView;
    }

    /**
     * Crea uno {@link StackPane} di fallback con la classe CSS indicata,
     * usato quando la risorsa grafica non è disponibile.
     *
     * @param cssClass la classe CSS da applicare al pannello.
     * @return lo stackpane configurato con dimensioni e stile.
     */
    private StackPane buildCssPane(String cssClass) {
        StackPane fallback = new StackPane();
        fallback.getStyleClass().add(cssClass);
        fallback.setPrefSize(CELL_SIZE, CELL_SIZE);
        return fallback;
    }

    /**
     * Restituisce la stringa di stile inline per colorare la barra degli HP.
     *
     * @param hpPercent la percentuale di HP correnti, tra 0.0 e 1.0.
     * @return la stringa di stile JavaFX da applicare alla {@link ProgressBar}.
     */
    private String getHpBarColor(double hpPercent) {
        if (hpPercent > 0.5)  return "-fx-accent: #44ff44;";
        if (hpPercent > 0.25) return "-fx-accent: #ffaa00;";
        return "-fx-accent: #ff4444;";
    }

    /**
     * Restituisce la sigla testuale dell'unità usata come fallback grafico.
     *
     * @param unit l'unità di cui ricavare la sigla.
     * @return la sigla testuale corrispondente alla classe dell'unità.
     */
    private String getUnitSymbol(Unit unit) {
        return switch (unit.getUnitClass()) {
            case WARRIOR -> "W";
            case MAGE    -> "M";
            case ARCHER  -> "A";
            case KNIGHT  -> "K";
            case THIEF   -> "T";
            case HEALER  -> "H";
        };
    }
}
package it.unicam.cs.mpgc.rpg123743.ui.javafx.controller;

import it.unicam.cs.mpgc.rpg123743.model.*;
import it.unicam.cs.mpgc.rpg123743.ui.javafx.view.MapCellNode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Gestisce esclusivamente la logica di rendering della UI della battaglia.
 * Si occupa di aggiornare la griglia, il log di combattimento e il pannello informativo.
 *
 * <p>Questa classe non contiene alcuna logica di gioco: riceve dati già calcolati
 * (es. celle raggiungibili, unità selezionata) e si limita a tradurli in nodi grafici.
 * L'orchestrazione della logica (movimento, combattimento, turni) è responsabilità
 * del controller che la utilizza (es. {@code BattleController}), mantenendo qui
 * una netta separazione tra presentazione e regole di gioco.</p>
 */
public class BattleUIManager {

    private final GridPane mapGrid;
    private final TextArea combatLog;
    private final VBox unitInfoPanel;
    private final Label turnLabel;

    /**
     * Costruisce un nuovo gestore della UI di battaglia, collegandolo ai nodi
     * grafici definiti nell'FXML della schermata di battaglia.
     *
     * @param mapGrid       il pannello a griglia su cui disegnare la mappa (non nullo).
     * @param combatLog     l'area di testo dove appendere il log di combattimento (non nulla).
     * @param unitInfoPanel il pannello laterale con le informazioni dell'unità selezionata (non nullo).
     * @param turnLabel     l'etichetta che mostra il turno corrente (non nulla).
     * @throws NullPointerException se uno dei parametri è nullo.
     */
    public BattleUIManager(GridPane mapGrid, TextArea combatLog, VBox unitInfoPanel, Label turnLabel) {
        this.mapGrid = Objects.requireNonNull(mapGrid, "Map grid cannot be null.");
        this.combatLog = Objects.requireNonNull(combatLog, "Combat log cannot be null.");
        this.unitInfoPanel = Objects.requireNonNull(unitInfoPanel, "Unit info panel cannot be null.");
        this.turnLabel = Objects.requireNonNull(turnLabel, "Turn label cannot be null.");
    }

    /**
     * Ricostruisce la griglia grafica in base allo stato attuale della mappa.
     * Ogni cella viene ridisegnata da zero, evidenziando le posizioni raggiungibili,
     * quelle attaccabili e l'eventuale unità selezionata.
     *
     * @param map         la mappa di battaglia corrente.
     * @param reachable   l'insieme delle posizioni raggiungibili dall'unità selezionata.
     * @param attackable  l'insieme delle posizioni attaccabili dall'unità selezionata.
     * @param selected    l'unità attualmente selezionata, o {@code null} se nessuna.
     * @param clickAction l'azione da invocare quando una cella viene cliccata.
     */
    public void refreshGrid(BattleMap map, Set<Position> reachable, Set<Position> attackable,
                            Unit selected, Consumer<Position> clickAction) {
        mapGrid.getChildren().clear();
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getCols(); c++) {
                Position pos = new Position(r, c);
                boolean isSelected = (selected != null && selected.getPosition().equals(pos));

                MapCellNode node = new MapCellNode(map.getCell(pos), pos,
                        reachable.contains(pos), attackable.contains(pos), isSelected);
                node.setOnMouseClicked(e -> clickAction.accept(pos));
                mapGrid.add(node, c, r);
            }
        }
    }

    /**
     * Aggiorna il pannello laterale con le informazioni dell'unità selezionata,
     * mostrando nome, fazione, statistiche principali e i consumabili presenti
     * nell'inventario, ciascuno con un bottone per utilizzarlo immediatamente.
     *
     * @param unit       l'unità di cui mostrare le informazioni (non nulla).
     * @param useAction  azione invocata con il consumabile scelto quando il
     *                    giocatore preme il relativo bottone "Use".
     */
    public void showUnitInfo(Unit unit, Consumer<Consumable> useAction) {
        unitInfoPanel.getChildren().clear();
        Label header = new Label(unit.getName());
        header.getStyleClass().add("panel-header");
        Stats s = unit.getStats();
        unitInfoPanel.getChildren().addAll(header,
                new Label(unit.getFaction() == Faction.PLAYER ? "[Player]" : "[Enemy]"),
                new Label("HP: " + s.getCurrentHp() + "/" + s.getMaxHp()),
                new Label("ATK: " + s.getAttack()),
                new Label("DEF: " + s.getDefence()),
                new Label("MOV: " + s.getMovement())
        );

        List<Consumable> consumables = unit.getInventory().stream()
                .filter(Consumable.class::isInstance)
                .map(Consumable.class::cast)
                .toList();

        if (!consumables.isEmpty()) {
            Label itemsHeader = new Label("Items:");
            itemsHeader.getStyleClass().add("panel-subheader");
            unitInfoPanel.getChildren().add(itemsHeader);

            for (Consumable consumable : consumables) {
                HBox row = new HBox(8);
                Label itemLabel = new Label(consumable.getName() + " (" + consumable.getDurability() + ")");
                Button useButton = new Button("Use");
                useButton.setOnAction(e -> useAction.accept(consumable));
                row.getChildren().addAll(itemLabel, useButton);
                unitInfoPanel.getChildren().add(row);
            }
        }
    }

    /**
     * Aggiunge una riga di testo al log di combattimento.
     *
     * @param message il messaggio da registrare.
     */
    public void log(String message) {
        combatLog.appendText(message + "\n");
    }

    /**
     * Aggiorna l'etichetta che mostra il turno corrente.
     *
     * @param text il testo da mostrare.
     */
    public void updateTurnLabel(String text) {
        turnLabel.setText(text);
    }
}
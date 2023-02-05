package de.jonas.graphingcalculator.constant;

import de.jonas.graphingcalculator.gui.CalculatorGui;
import de.jonas.graphingcalculator.gui.FunctionGui;
import de.jonas.graphingcalculator.gui.ValueTableGui;
import de.jonas.graphingcalculator.object.CalculatorField;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Ein {@link CalculatorActionType Typ} wird jeweils aus einem Pfad zu einem Bild, einem Titel, einem
 * {@link ActionListener} und einem Count - bei 0 beginnend - erzeugt. Jeder Typ stellt eine Aktion dar, die der
 * Taschenrechner ausführen kann und welche im {@link de.jonas.graphingcalculator.gui.MainGui Haupt-Fenster} angezeigt
 * wird.
 */
@NotNull
public enum CalculatorActionType {

    //<editor-fold desc="VALUES">
    /** Der {@link CalculatorActionType Typ} für den normalen Taschenrechner. */
    CALCULATOR_ACTION_TYPE(
        "calcFunction.png",
        "Rechnen",
        e -> new CalculatorGui(new CalculatorField[]{
            new CalculatorField("sin"),
            new CalculatorField("cos"),
            new CalculatorField("tan"),
            new CalculatorField("√"),
            new CalculatorField("π"),
            new CalculatorField("C"),
            new CalculatorField("x^y"),
            new CalculatorField("("),
            new CalculatorField(")"),
            new CalculatorField("⬅"),
            new CalculatorField("7"),
            new CalculatorField("8"),
            new CalculatorField("9"),
            new CalculatorField("-"),
            new CalculatorField("÷"),
            new CalculatorField("4"),
            new CalculatorField("5"),
            new CalculatorField("6"),
            new CalculatorField("+"),
            new CalculatorField("×"),
            new CalculatorField("1"),
            new CalculatorField("2"),
            new CalculatorField("3"),
            new CalculatorField(","),
            new CalculatorField("=", true),
            new CalculatorField("log"),
            new CalculatorField("0"),
            new CalculatorField("ln"),
            new CalculatorField("e"),
        }),
        0
    ),
    /** Der {@link CalculatorActionType Typ} für das Zeichnen einer Funktion. */
    DRAW_FUNCTION_ACTION_TYPE(
        "drawFunction.png",
        "Zeichnen",
        e -> new FunctionGui(),
        1
    ),
    /** Der {@link CalculatorActionType Typ} für das Erstellen einer Wertetabelle. */
    VALUE_TABLE_FUNCTION(
        "valueTableFunction.png",
        "Wertetabelle",
        e -> new ValueTableGui(),
        2
    );
    //</editor-fold>


    //<editor-fold desc="CONSTANTS">
    /** Die Größe jeder Auswahlmöglichkeit an Aktionen, die ausgeführt werden sollen. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int CALC_ACTION_SIZE = 150;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der Button, der diesen {@link CalculatorActionType Typ} repräsentiert und angezeigt wird. */
    @Getter
    private final JButton button;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue und vollständig unabhängige Instanz einer {@link CalculatorActionType}. Ein
     * {@link CalculatorActionType Typ} wird jeweils aus einem Pfad zu einem Bild, einem Titel, einem
     * {@link ActionListener} und einem Count - bei 0 beginnend - erzeugt. Jeder Typ stellt eine Aktion dar, die der
     * Taschenrechner ausführen kann und welche im {@link de.jonas.graphingcalculator.gui.MainGui Haupt-Fenster}
     * angezeigt wird.
     *
     * @param ressourceName  Der Pfad zu dem Bild, welches in Form eines Icons auf dem Button angezeigt werden soll.
     * @param displayName    Der Name, der unter dem Icon auf dem Button angezeigt werden soll.
     * @param actionListener Der {@link ActionListener}, welcher beim Anklicken des Buttons ausgeführt wird.
     * @param count          Der Count für die Positionierung des Buttons.
     */
    CalculatorActionType(
        @NotNull final String ressourceName,
        @NotNull final String displayName,
        @NotNull final ActionListener actionListener,
        @Range(from = 0, to = Integer.MAX_VALUE) final int count
    ) {
        this.button = new JButton(displayName);
        button.setBounds(
            10 + ((count % 2) * (CALC_ACTION_SIZE + 10)),
            10 + ((count / 2) * (CALC_ACTION_SIZE + 10)),
            CALC_ACTION_SIZE,
            CALC_ACTION_SIZE
        );
        button.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/" + ressourceName))));
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.addActionListener(actionListener);
    }
    //</editor-fold>

}

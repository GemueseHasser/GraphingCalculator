package de.jonas.graphingcalculator.gui;

import de.jonas.graphingcalculator.GraphingCalculator;
import de.jonas.graphingcalculator.constant.CalculatorActionType;
import de.jonas.graphingcalculator.object.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.JFrame;

/**
 * Das {@link MainGui} ist eine Instanz eines {@link Gui} und stellt das Menü des Taschenrechners dar, in welchem man
 * die Aktion auswählen kann, die man mit dem {@link GraphingCalculator} durchführen möchte.
 */
@NotNull
public final class MainGui extends Gui {

    //<editor-fold desc="CONSTANTS">
    /** Der Titel dieses Fensters. */
    @NotNull
    private static final String TITLE = "Grafischer Taschenrechner";
    /** Die Breite dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int WIDTH = 340;
    /** Die Höhe dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int HEIGHT = 500;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link MainGui}. Das {@link MainGui} ist eine Instanz eines {@link Gui} und
     * stellt das Menü des Taschenrechners dar, in welchem man die Aktion auswählen kann, die man mit dem
     * {@link GraphingCalculator} durchführen möchte.
     */
    public MainGui() {
        super(TITLE, WIDTH, HEIGHT);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (@NotNull final CalculatorActionType calculatorActionType : CalculatorActionType.values()) {
            super.add(calculatorActionType.getButton());
        }
    }
    //</editor-fold>

}

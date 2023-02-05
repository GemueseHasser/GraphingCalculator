package de.jonas.graphingcalculator.object;

import de.jonas.graphingcalculator.gui.CalculatorGui;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Ein {@link CalculatorField} stellt ein Feld zur Verfügung, welches die Eigenschaften eines Namens, einer Höhe und
 * einer konstanten Breite besitzt. Dieses Feld wird genutzt, um daraus Buttons zu erzeugen, mit dessen Hilfe der Nutzer
 * in dem {@link CalculatorGui} seine Eingabe tätigen kann.
 */
@NotNull
@RequiredArgsConstructor
public final class CalculatorField {

    //<editor-fold desc="CONSTANTS">
    /** Die Anzahl an Feldern in einer Zeile. */
    public static final int FIELDS_PER_ROW = 5;
    /** Die Standard-Breite eines {@link CalculatorField}. */
    public static final int DEFAULT_WIDTH = CalculatorGui.WIDTH / FIELDS_PER_ROW - 2;
    /** Die Standard-Höhe eines {@link CalculatorField}. */
    public static final int DEFAULT_HEIGHT = CalculatorGui.WIDTH / FIELDS_PER_ROW - 2;
    /** Eine spezielle - doppelt so große - Höhe eines {@link CalculatorField}. */
    private static final int EXTRA_HEIGHT = CalculatorGui.WIDTH / FIELDS_PER_ROW * 2 - 2;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der Name dieses {@link CalculatorField}. */
    @Getter
    private final String name;
    /** Der Zustand, ob dieses Feld extra hoch sein soll. */
    private final boolean extraHeight;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue und vollständig unabhängige Instanz eines {@link CalculatorField}, jedoch ausschließlich
     * mithilfe eines Namens, da dies eine standard-Initialisierung ist und somit auch die Höhe des Feldes standardmäßig
     * sein soll. Ein {@link CalculatorField} stellt ein Feld zur Verfügung, welches die Eigenschaften eines Namens,
     * einer Höhe und einer konstanten Breite besitzt. Dieses Feld wird genutzt, um daraus Buttons zu erzeugen, mit
     * dessen Hilfe der Nutzer in dem {@link CalculatorGui} seine Eingabe tätigen kann.
     *
     * @param name Der Name dieses Feldes.
     */
    public CalculatorField(@NotNull final String name) {
        this.name = name;
        this.extraHeight = false;
    }
    //</editor-fold>


    /**
     * Gibt die Höhe dieses Feldes, abhängig von dem Zustand {@code extraHeight} zurück.
     *
     * @return Die Höhe dieses Feldes, abhängig von dem Zustand {@code extraHeight}.
     */
    public int getHeight() {
        return extraHeight ? EXTRA_HEIGHT : DEFAULT_HEIGHT;
    }

}

package de.jonas.graphingcalculator.object;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.JFrame;

/**
 * Ein {@link Gui} stellt eine grafische Oberfläche, also ein Fenster dar, welches mit
 * {@link java.awt.Component Komponenten} ausgestattet werden kann. Das Gui hat gewisse Eigenschaften, die
 * voreingestellt sind.
 */
@NotNull
public abstract class Gui extends JFrame {

    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link Gui}. Ein {@link Gui} stellt eine grafische Oberfläche, also ein Fenster
     * dar, welches mit {@link java.awt.Component Komponenten} ausgestattet werden kann. Das Gui hat gewisse
     * Eigenschaften, die voreingestellt sind.
     *
     * @param title  Der Titel des Fensters.
     * @param width  Die Breite des Fensters.
     * @param height Die Höhe des Fensters.
     */
    public Gui(
        @NotNull final String title,
        @Range(from = 0, to = Integer.MAX_VALUE) final int width,
        @Range(from = 0, to = Integer.MAX_VALUE) final int height
    ) {
        // create super instance
        super(title);

        // set jframe properties
        super.setBounds(0, 0, width, height);
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        super.setResizable(false);
        super.setLayout(null);
    }
    //</editor-fold>

}

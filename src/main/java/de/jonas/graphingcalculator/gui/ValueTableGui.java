package de.jonas.graphingcalculator.gui;

import de.jonas.graphingcalculator.handler.FunctionHandler;
import de.jonas.graphingcalculator.object.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Ein {@link ValueTableGui} stellt eine Instanz eines {@link Gui} dar, welches eine grafische Oberfläche darstellt, auf
 * der eine Wertetabelle angezeigt werden kann.
 */
@NotNull
public final class ValueTableGui extends Gui {

    //<editor-fold desc="CONSTANTS">
    /** Der Titel dieses Fensters. */
    @NotNull
    private static final String TITLE = "Wertetabelle";
    /** Die Breite dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int WIDTH = 800;
    /** Die Höhe dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int HEIGHT = 850;
    /** Die Breite der Wertetabelle in diesem Fenster. */
    @Range(from = 0, to = WIDTH)
    private static final int VALUE_TABLE_WIDTH = WIDTH - 60;
    /** Die Höhe jeder einzelnen Zeile der Wertetabelle. */
    private static final int VALUE_TABLE_ROW_HEIGHT = 30;
    /** Die standardmäßig in diesem Fenster genutzte Schriftart. */
    @NotNull
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 15);
    //</editor-fold>


    //<editor-fold desc="STATIC FIELDS">
    /** Die als letztes verwendete Funktion des Nutzers. */
    @NotNull
    private static String lastFunction = "";
    /** Der als letztes angegebene kleinste x-Wert der Wertetabelle. */
    @NotNull
    private static String lastXMin = "-5";
    /** Der als letztes angegebene größte x-Wert der Wertetabelle. */
    @NotNull
    private static String lastXMax = "5";
    /** Die als letztes angegebene Schrittweite der Wertetabelle. */
    @NotNull
    private static String lastIncrement = "1";
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Textfeld, in welchem der kleinste x-Wert der Wertetabelle angegeben wird. */
    @NotNull
    private final JTextField xMinField = new JTextField(lastXMin, 10);
    /** Das Textfeld, in welchem der größte x-Wert der Wertetabelle angegeben wird. */
    @NotNull
    private final JTextField xMaxField = new JTextField(lastXMax, 10);
    /** Das Textfeld, in welchem die Schrittweite der Wertetabelle angegeben wird. */
    @NotNull
    private final JTextField incrementField = new JTextField(lastIncrement, 10);
    /** Die {@link Map}, in der alle Werte dieser Wertetabelle abgespeichert werden. */
    @NotNull
    private final NavigableMap<Double, Double> values = new TreeMap<>();
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link ValueTableGui}. Ein {@link ValueTableGui} stellt eine Instanz eines
     * {@link Gui} dar, welches eine grafische Oberfläche darstellt, auf der eine Wertetabelle angezeigt werden kann.
     */
    public ValueTableGui() {
        super(TITLE, WIDTH, HEIGHT);

        // create function message panel
        final JPanel[] messagePanel = new JPanel[4];

        messagePanel[0] = new JPanel();
        messagePanel[1] = new JPanel();
        messagePanel[2] = new JPanel();
        messagePanel[3] = new JPanel();

        // add function field
        final JTextField functionField = new JTextField(lastFunction, 10);
        messagePanel[0].add(new JLabel("f(x) = "));
        messagePanel[0].add(functionField);

        // add x-min field
        messagePanel[1].add(new JLabel("x-min: "));
        messagePanel[1].add(this.xMinField);

        // add x-max field
        messagePanel[2].add(new JLabel("x-max: "));
        messagePanel[2].add(this.xMaxField);

        // add increment-field
        messagePanel[3].add(new JLabel("Schrittweite: "));
        messagePanel[3].add(this.incrementField);

        // create dialog
        final int functionDrawOption = JOptionPane.showConfirmDialog(
            null,
            messagePanel,
            "Wertetabelle anlegen",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (functionDrawOption != JOptionPane.OK_OPTION) return;

        // create new function handler to calculate function values
        final FunctionHandler functionHandler = new FunctionHandler(
            functionField.getText().replaceAll(",", "."),
            0
        );

        // set last values
        lastFunction = functionHandler.getFunction();
        lastXMin = String.valueOf(getXMin());
        lastXMax = String.valueOf(getXMax());
        lastIncrement = String.valueOf(getIncrement());

        // create function label to display the function
        final JLabel functionLabel = new JLabel(functionHandler.getFunction(), JLabel.CENTER);
        functionLabel.setBounds(0, 0, WIDTH, 50);
        functionLabel.setFont(DEFAULT_FONT.deriveFont(23F));

        // fill value-map with function-values
        for (double i = getXMin(); i <= getXMax(); i += getIncrement()) {
            this.values.put(i, functionHandler.getFunctionValue(i));
        }

        // create scrollable value-table
        final JLabel valueTableLabel = new JLabel(new ImageIcon(getValueTable()));
        final JScrollPane scrollPane = new JScrollPane(valueTableLabel);
        scrollPane.setBounds((WIDTH - VALUE_TABLE_WIDTH - 40) / 2, 50, VALUE_TABLE_WIDTH + 25, HEIGHT - 120);

        super.add(functionLabel);
        super.add(scrollPane);
        super.setVisible(true);
    }
    //</editor-fold>


    /**
     * Gibt die Wertetabelle, die angezeigt werden soll in Form eines Bildes zurück, damit diese Tabelle als festes
     * Objekt unveränderbar angezeigt werden kann.
     *
     * @return Die Wertetabelle, die angezeigt werden soll in Form eines Bildes, damit diese Tabelle als festes Objekt
     *     unveränderbar angezeigt werden kann.
     */
    @NotNull
    private Image getValueTable() {
        // create new image
        final BufferedImage image = new BufferedImage(
            VALUE_TABLE_WIDTH,
            this.values.size() * VALUE_TABLE_ROW_HEIGHT,
            BufferedImage.TYPE_INT_ARGB
        );

        // create image graphics
        final Graphics g = image.createGraphics();
        g.setFont(DEFAULT_FONT);

        // calculate value table height
        final int valueTableHeight = this.values.size() * VALUE_TABLE_ROW_HEIGHT;

        int count = 1;

        for (@NotNull final Map.Entry<Double, Double> valueTableEntry : this.values.entrySet()) {
            // get x- and y-coordinate
            final double x = Math.round(valueTableEntry.getKey() * 100000D) / 100000D;
            final double y = Math.round(valueTableEntry.getValue() * 100000D) / 100000D;

            // mark background light-gray
            if (count % 2 == 0) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(
                    1,
                    count * VALUE_TABLE_ROW_HEIGHT - VALUE_TABLE_ROW_HEIGHT + 1,
                    VALUE_TABLE_WIDTH - 1,
                    VALUE_TABLE_ROW_HEIGHT - 1
                );
            }

            // draw current value table entry
            g.setColor(Color.BLACK);
            g.drawLine(
                1,
                count * VALUE_TABLE_ROW_HEIGHT,
                VALUE_TABLE_WIDTH - 1,
                count * VALUE_TABLE_ROW_HEIGHT
            );
            g.drawString(Double.toString(x), 50, count * VALUE_TABLE_ROW_HEIGHT - 10);
            g.drawString(Double.toString(y), VALUE_TABLE_WIDTH / 2 + 50, count * VALUE_TABLE_ROW_HEIGHT - 10);

            count++;
        }

        // draw value table framework
        g.setColor(Color.BLACK);
        g.drawLine(1, 1, VALUE_TABLE_WIDTH - 1, 1);
        g.drawLine(1, 1, 1, valueTableHeight - 1);
        g.drawLine(1, valueTableHeight - 1, VALUE_TABLE_WIDTH - 1, valueTableHeight - 1);
        g.drawLine(VALUE_TABLE_WIDTH - 1, 1, VALUE_TABLE_WIDTH - 1, valueTableHeight - 1);
        g.drawLine(VALUE_TABLE_WIDTH / 2, 1, VALUE_TABLE_WIDTH / 2, valueTableHeight - 1);

        return image;
    }

    /**
     * Gibt den kleinsten x-Wert der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers zurück.
     *
     * @return Der kleinste x-Wert der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers.
     */
    private int getXMin() {
        try {
            return Integer.parseInt(this.xMinField.getText());
        } catch (@NotNull final NumberFormatException ignored) {
            return -5;
        }
    }

    /**
     * Gibt den größten x-Wert der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers zurück.
     *
     * @return Der größte x-Wert der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers.
     */
    private int getXMax() {
        try {
            return Integer.parseInt(this.xMaxField.getText());
        } catch (@NotNull final NumberFormatException ignored) {
            return 5;
        }
    }

    /**
     * Gibt die Schrittweite der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers zurück.
     *
     * @return Die Schrittweite der Wertetabelle unter Berücksichtigung einer falschen Eingabe des Nutzers.
     */
    private double getIncrement() {
        try {
            return Double.parseDouble(this.incrementField.getText().replaceAll(",", "."));
        } catch (@NotNull final NumberFormatException ignored) {
            return 1D;
        }
    }

}

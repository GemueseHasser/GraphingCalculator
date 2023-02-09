package de.jonas.graphingcalculator.object;

import de.jonas.graphingcalculator.handler.FunctionHandler;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Mit einem {@link DrawFunction} lässt sich eine bestimmte Funktion, deren Werte in Form von X- und Y-Koordinate in
 * einer {@link Map} abgespeichert übergeben werden, zeichnen.
 */
@NotNull
public final class DrawFunction extends JLabel {

    //<editor-fold desc="CONSTANTS">
    /** Die Standard-Schriftart dieses Fensters. */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 11);
    /** Der linke und rechte Abstand, den die x-Achse vom Rand dieses Objekts besitzt. */
    private static final int X_MARGIN = 50;
    /** Der obere und untere Abstand, den die y-Achse vom Rand dieses Objekts besitzt. */
    private static final int Y_MARGIN = 50;
    /** Der Abstand zwischen den einzelnen Beschriftungen des Koordinatensystems. */
    private static final int LABEL_MARGIN = 35;
    /** Die Anzahl an Beschriftungen der x-Achse. */
    private static final int LABEL_AMOUNT_X = 10;
    /** Die Anzahl an Beschriftungen der y-Achse. */
    private static final int LABEL_AMOUNT_Y = 10;
    /** Die Größe jeder Markierung. */
    private static final int MARK_SIZE = 10;
    /** Die Menge an Ableitungen, die angeboten werden soll. */
    private static final int DERIVATION_AMOUNT = 4;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der {@link FunctionHandler}, dessen Funktion gezeichnet wird. */
    @NotNull
    private final FunctionHandler functionHandler;
    /** Alle Funktionswerte, aus denen dann eine Funktion gezeichnet wird. */
    @NotNull
    private final NavigableMap<Double, Double> function;
    /** Die verschiedenen Ableitungen der Funktion gekoppelt an den Zustand, ob sie angezeigt werden sollen. */
    @Getter
    @NotNull
    private final LinkedHashMap<Integer, Derivation> derivations = new LinkedHashMap<>();
    /** Eine Liste, die alle Punkte beinhaltet, die besonders hervorgehoben werden sollen in der Funktion. */
    @NotNull
    private final LinkedList<Point> markedPoints = new LinkedList<>();
    /** Die Skalierung für die x-Achse. */
    @Range(from = LABEL_AMOUNT_X, to = Integer.MAX_VALUE)
    private final int scaleX;
    /** Die Skalierung für die y-Achse. */
    @Range(from = LABEL_AMOUNT_Y, to = Integer.MAX_VALUE)
    private final int scaleY;
    /** Die x-Koordinate der Maus. */
    @Getter
    @Nullable
    private Point mouse;
    /** Die Funktion der Tangente, die angelegt werden soll. */
    @Setter
    @Nullable
    private String tangentFunction;
    /** Der Zustand, ob die Nullstellen angezeigt werden sollen oder nicht. */
    @Setter
    private boolean enableRoots;
    /** Der Zustand, ob die Extremstellen angezeigt werden sollen oder nicht. */
    @Setter
    private boolean enableExtremes;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue und vollständig unabhängige Instanz eines {@link DrawFunction}. Mit einem {@link DrawFunction}
     * lässt sich eine bestimmte Funktion, deren Werte in Form von X- und Y-Koordinate in einer {@link Map}
     * abgespeichert übergeben werden, zeichnen.
     *
     * @param functionHandler Der {@link FunctionHandler}, dessen Funktion gezeichnet werden soll.
     * @param scaleX          Die Skalierung für die x-Achse.
     * @param scaleY          Die Skalierung für die y-Achse.
     */
    public DrawFunction(
        @NotNull final FunctionHandler functionHandler,
        @Range(from = 0, to = Integer.MAX_VALUE) final int scaleX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int scaleY
    ) {
        // create temp map
        final NavigableMap<Double, Double> filteredFunction = new TreeMap<>();

        // calculate draw tolerance
        final double xTolerance = (double) scaleX / 10;

        // filter function values
        for (@NotNull final Map.Entry<Double, Double> functionEntry : functionHandler.getFunctionValues().entrySet()) {
            // get current values from entry
            final double x = functionEntry.getKey();
            final double y = functionEntry.getValue();

            // check if values are out of bounds
            if (x > scaleX + xTolerance || x < -scaleX - xTolerance) {
                continue;
            }

            // mark values as filtered
            filteredFunction.put(x, y);
        }

        // set derivations
        NavigableMap<Double, Double> lastDerivation = FunctionHandler.getDerivationValues(functionHandler.getFunctionValues());

        for (int i = 0; i < DERIVATION_AMOUNT; i++) {
            this.derivations.put(i, new Derivation(lastDerivation));
            lastDerivation = FunctionHandler.getDerivationValues(lastDerivation);
        }

        // initialize variables
        this.functionHandler = functionHandler;
        this.function = filteredFunction;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    //</editor-fold>

    /**
     * Gibt die aktuellen Zeichnungen dieses {@link DrawFunction} in Form eines Bildes zurück.
     *
     * @return Die aktuellen zeichnungen dieses {@link DrawFunction} in Form eines Bildes.
     */
    public BufferedImage getGraphicsAsImage() {
        final BufferedImage image = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics imageGraphics = image.createGraphics();

        drawGraphics(imageGraphics);

        return image;
    }

    /**
     * Fügt einen bestimmten Punkt zu den Punkten hinzu, die besonders markiert werden sollen in der Funktion.
     *
     * @param x Der x-Wert, zu dem der Punkt besonders markiert werden soll.
     */
    public void addMarkedPoint(final double x) {
        if (Double.isNaN(this.functionHandler.getFunctionValue(x))) return;

        this.markedPoints.addLast(new Point(x, this.functionHandler.getFunctionValue(x)));
    }

    /**
     * Entfernt, solange sich noch Einträge in der Liste der markierten Punkte befinden, den letzten Eintrag dieser
     * Liste.
     */
    public void removeLastMarkedPoint() {
        if (this.markedPoints.isEmpty()) return;

        this.markedPoints.removeLast();
    }

    /**
     * Verarbeitet das Anklicken der Maus-Taste, setzt somit vorübergehend den aktuellen Punkt der Maus.
     *
     * @param mouseX Die x-Koordinate der Maus.
     */
    public void handleMousePressed(@Range(from = 0, to = Integer.MAX_VALUE) final int mouseX) {
        final double x = getFunctionX(mouseX);

        this.mouse = new Point(x, this.functionHandler.getFunctionValue(x));
    }

    /**
     * Verarbeitet das Loslassen der Maus-Taste und entfernt somit den zuvor gesetzten Punkt der Maus wieder.
     */
    public void handleMouseReleased() {
        this.mouse = null;
    }

    /**
     * Zeichnet alle Grafiken.
     *
     * @param g Das {@link Graphics Graphics-Objekt}, mit dem alle Grafiken gezeichnet werden sollen.
     */
    private void drawGraphics(@NotNull final Graphics g) {
        // draw background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, super.getWidth(), super.getHeight());

        // draw function
        g.setColor(Color.WHITE);
        g.setFont(DEFAULT_FONT.deriveFont(17F));
        g.drawString("f(x) = " + this.functionHandler.getFunction(), 20, 30);

        final int yAxisX = super.getWidth() / 2;
        final int xAxisY = super.getHeight() / 2;

        // draw coordinate system
        g.setFont(DEFAULT_FONT);
        g.drawLine(
            X_MARGIN - 20,
            xAxisY,
            super.getWidth() - X_MARGIN + 20,
            xAxisY
        );
        g.drawLine(
            yAxisX,
            super.getHeight() - Y_MARGIN - 5,
            yAxisX,
            Y_MARGIN
        );

        // draw arrows
        g.drawLine(super.getWidth() - X_MARGIN + 20, xAxisY, super.getWidth() - X_MARGIN + 10, xAxisY - 5);
        g.drawLine(super.getWidth() - X_MARGIN + 20, xAxisY, super.getWidth() - X_MARGIN + 10, xAxisY + 5);
        g.drawLine(yAxisX, Y_MARGIN, yAxisX - 5, Y_MARGIN + 10);
        g.drawLine(yAxisX, Y_MARGIN, yAxisX + 5, Y_MARGIN + 10);

        // draw x labels
        for (int i = -LABEL_AMOUNT_X; i <= LABEL_AMOUNT_X; i++) {
            g.drawLine(
                yAxisX + i * LABEL_MARGIN,
                xAxisY - 5,
                yAxisX + i * LABEL_MARGIN,
                xAxisY + 5
            );

            if (i == 0) continue;

            g.drawString(
                String.valueOf(Math.round((((double) this.scaleX / LABEL_AMOUNT_X) * i) * 10D) / 10D),
                yAxisX + i * LABEL_MARGIN - 5,
                xAxisY + 20
            );
        }

        // draw y labels
        for (int i = -LABEL_AMOUNT_Y; i <= LABEL_AMOUNT_Y; i++) {
            g.drawLine(
                yAxisX - 5,
                xAxisY - i * LABEL_MARGIN,
                yAxisX + 5,
                xAxisY - i * LABEL_MARGIN
            );

            if (i == 0) continue;

            g.drawString(
                String.valueOf(Math.round((((double) this.scaleY / LABEL_AMOUNT_Y) * i) * 10D) / 10D),
                yAxisX - 40,
                (xAxisY - i * LABEL_MARGIN) + 5
            );
        }

        // draw function
        g.setColor(Color.RED);
        drawFunction(g, this.function, yAxisX, xAxisY);

        // check if roots or extremes are enabled
        g.setColor(Color.BLUE);
        if (this.enableRoots) drawRoots(g, yAxisX, xAxisY);
        if (this.enableExtremes) drawExtremes(g, yAxisX, xAxisY);

        // draw marked points
        for (@NotNull final Point point : this.markedPoints) {
            final int x = getValueX(point.getX());
            final int y = getValueY(point.getY());

            g.fillOval(
                x + (yAxisX - X_MARGIN) - (MARK_SIZE / 2),
                y - (xAxisY - Y_MARGIN) - (MARK_SIZE / 2),
                MARK_SIZE,
                MARK_SIZE
            );

            final double xCoordinate = Math.round(point.getX() * 100D) / 100D;
            final double yCoordinate = Math.round(point.getY() * 100D) / 100D;

            g.setFont(DEFAULT_FONT.deriveFont(12F));
            g.drawString(
                "(" + xCoordinate + " | " + yCoordinate + ")",
                x + (yAxisX - X_MARGIN) - 10,
                y - (xAxisY - Y_MARGIN) - 15
            );
        }

        // draw mouse
        if (this.mouse != null) drawMouse(g, yAxisX, xAxisY);

        // draw tangent
        if (this.tangentFunction != null) drawTangent(g, yAxisX, xAxisY);

        // check if derivations are enabled
        g.setColor(Color.GREEN);

        for (@NotNull final Map.Entry<Integer, Derivation> derivationEntry : this.derivations.entrySet()) {
            final Derivation derivation = derivationEntry.getValue();

            if (!derivation.isDraw()) continue;

            drawFunction(g, derivation.getDerivationValues(), yAxisX, xAxisY);
        }
    }

    /**
     * Gibt die x-Koordinate angepasst an die Skalierung der Funktion wieder.
     *
     * @param x Die x-Koordinate in dem Fenster.
     *
     * @return Die x-Koordinate angepasst an die Skalierung der Funktion.
     */
    private double getFunctionX(final int x) {
        return ((x - X_MARGIN) / (double) LABEL_MARGIN * ((double) this.scaleX / LABEL_AMOUNT_X)) - this.scaleX;
    }

    /**
     * Berechnet aus einem x-Wert der Funktion den entsprechenden finalen x-Wert, den diese Stelle in dem
     * Koordinatensystem bzw. auf diesem Objekt widerspiegelt.
     *
     * @param x Der x-Wert der Funktion.
     *
     * @return Der finale x-Wert, der dem x-Wert der Funktion entspricht.
     */
    private int getValueX(final double x) {
        return (int) (X_MARGIN + (x * LABEL_MARGIN / ((double) this.scaleX / LABEL_AMOUNT_X)));
    }

    /**
     * Berechnet aus einem y-Wert der Funktion den entsprechenden finalen y-Wert, den diese Stelle in dem
     * Koordinatensystem bzw. auf diesem Objekt widerspiegelt.
     *
     * @param y Der y-Wert der Funktion.
     *
     * @return Der finale y-Wert, der dem y-Wert der Funktion entspricht.
     */
    private int getValueY(final double y) {
        return (int) (super.getHeight() - Y_MARGIN - (y * LABEL_MARGIN / ((double) this.scaleY / LABEL_AMOUNT_Y)));
    }

    /**
     * Zeichnet alle Nullstellen der Funktion mit ihren Koordinaten ein.
     *
     * @param g      Das {@link Graphics Grafik-Objekt}, mit dem die Nullstellen eingezeichnet werden sollen.
     * @param yAxisX Die x-Koordinate der y-Achse.
     * @param xAxisY Die y-Koordinate der x-Achse.
     */
    private void drawRoots(
        @NotNull final Graphics g,
        @Range(from = 0, to = Integer.MAX_VALUE) final int yAxisX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int xAxisY
    ) {
        // get roots
        final Map<Double, Double> roots = this.functionHandler.getRoots();

        // draw roots
        for (@NotNull final Map.Entry<Double, Double> rootEntry : roots.entrySet()) {
            final int x = getValueX(rootEntry.getKey());
            final int y = getValueY(rootEntry.getValue());

            g.fillOval(
                x + (yAxisX - X_MARGIN) - (MARK_SIZE / 2),
                y - (xAxisY - Y_MARGIN) - (MARK_SIZE / 2),
                MARK_SIZE,
                MARK_SIZE
            );

            final double xCoordinate = Math.round(rootEntry.getKey() * 100D) / 100D;

            g.setFont(DEFAULT_FONT.deriveFont(12F));
            g.drawString(
                "(" + xCoordinate + ")",
                x + (yAxisX - X_MARGIN) - 10,
                y - (xAxisY - Y_MARGIN) - 15
            );
        }
    }

    /**
     * Zeichnet alle Extremstellen der Funktion mit ihren Koordinaten ein.
     *
     * @param g      Das {@link Graphics Grafik-Objekt}, mit dem die Nullstellen eingezeichnet werden sollen.
     * @param yAxisX Die x-Koordinate der y-Achse.
     * @param xAxisY Die y-Koordinate der x-Achse.
     */
    private void drawExtremes(
        @NotNull final Graphics g,
        @Range(from = 0, to = Integer.MAX_VALUE) final int yAxisX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int xAxisY
    ) {
        // get extremes
        final Map<Double, Double> extremes = this.functionHandler.getExtremes();

        // draw roots
        for (@NotNull final Map.Entry<Double, Double> extremeEntry : extremes.entrySet()) {
            final int x = getValueX(extremeEntry.getKey());
            final int y = getValueY(extremeEntry.getValue());

            g.fillOval(
                x + (yAxisX - X_MARGIN) - (MARK_SIZE / 2),
                y - (xAxisY - Y_MARGIN) - (MARK_SIZE / 2),
                MARK_SIZE,
                MARK_SIZE
            );

            final double xCoordinate = Math.round(extremeEntry.getKey() * 100D) / 100D;
            final double yCoordinate = Math.round(extremeEntry.getValue() * 100D) / 100D;

            g.setFont(DEFAULT_FONT.deriveFont(12F));
            g.drawString(
                "(" + xCoordinate + " | " + yCoordinate + ")",
                x + (yAxisX - X_MARGIN) - 10,
                y - (xAxisY - Y_MARGIN) - 15
            );
        }
    }

    /**
     * Zeichnet den zuletzt gespeicherten Punkt der Maus ein, solange dieser nicht {@code null} ist.
     *
     * @param g      Das {@link Graphics Grafik-Objekt}, mit dem die Nullstellen eingezeichnet werden sollen.
     * @param yAxisX Die x-Koordinate der y-Achse.
     * @param xAxisY Die y-Koordinate der x-Achse.
     */
    private void drawMouse(
        @NotNull final Graphics g,
        @Range(from = 0, to = Integer.MAX_VALUE) final int yAxisX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int xAxisY
    ) {
        assert this.mouse != null;
        final Point mouse = this.mouse;

        final int x = getValueX(mouse.getX());
        final int y = getValueY(mouse.getY());

        g.fillOval(
            x + (yAxisX - X_MARGIN) - (MARK_SIZE / 2),
            y - (xAxisY - Y_MARGIN) - (MARK_SIZE / 2),
            MARK_SIZE,
            MARK_SIZE
        );

        final double xCoordinate = Math.round(mouse.getX() * 100D) / 100D;
        final double yCoordinate = Math.round(mouse.getY() * 100D) / 100D;

        g.setFont(DEFAULT_FONT.deriveFont(12F));
        g.drawString(
            "(" + xCoordinate + " | " + yCoordinate + ")",
            x + (yAxisX - X_MARGIN) - 10,
            y - (xAxisY - Y_MARGIN) - 15
        );
    }

    /**
     * Zeichnet, falls eine Tangentengleichung vorhanden ist, die Tangente ein.
     *
     * @param g      Das {@link Graphics Grafik-Objekt}, mit dem die Nullstellen eingezeichnet werden sollen.
     * @param yAxisX Die x-Koordinate der y-Achse.
     * @param xAxisY Die y-Koordinate der x-Achse.
     */
    private void drawTangent(
        @NotNull final Graphics g,
        @Range(from = 0, to = Integer.MAX_VALUE) final int yAxisX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int xAxisY
    ) {
        if (this.tangentFunction == null) return;

        final NavigableMap<Double, Double> tangentValues = new TreeMap<>();

        // calculate and save tangent values
        for (double i = -this.scaleX; i < this.scaleX; i = Math.round((i + 0.001) * 1000D) / 1000D) {
            tangentValues.put(i, FunctionHandler.eval(this.tangentFunction.replaceAll("x", "(" + i + ")")));
        }

        // draw tangent
        drawFunction(g, tangentValues, yAxisX, xAxisY);

        // display function
        g.setColor(Color.WHITE);
        g.setFont(DEFAULT_FONT.deriveFont(17F));
        g.drawString("t(x) = " + this.tangentFunction, 20, 60);
    }

    /**
     * Zeichnet eine Funktion mithilfe von beliebig vielen Funktionswerten.
     *
     * @param g              Das {@link Graphics Grafik-Objekt}, mit dem die Nullstellen eingezeichnet werden sollen.
     * @param functionValues Alle Funktionswerte, die genutzt werden sollen, um die Funktion zu zeichnen.
     * @param yAxisX         Die x-Koordinate der y-Achse.
     * @param xAxisY         Die y-Koordinate der x-Achse.
     */
    private void drawFunction(
        @NotNull final Graphics g,
        @NotNull final NavigableMap<Double, Double> functionValues,
        @Range(from = 0, to = Integer.MAX_VALUE) final int yAxisX,
        @Range(from = 0, to = Integer.MAX_VALUE) final int xAxisY
    ) {
        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
            if (functionValue.getValue().isNaN()) continue;

            // get current values
            final double x = functionValue.getKey();
            final double y = functionValue.getValue();

            // check if next entry is preset
            if (functionValues.higherEntry(x) == null) break;

            // get next entry
            final Map.Entry<Double, Double> nextEntry = functionValues.higherEntry(x);

            // get next values
            final double nextX = nextEntry.getKey();
            final double nextY = nextEntry.getValue();

            // skip (+ to -) or (- to +)
            if ((y > 0 && nextY < 0) || (y < 0 && nextY > 0)) continue;

            // draw line
            g.drawLine(
                getValueX(x) + (yAxisX - X_MARGIN),
                getValueY(y) - (xAxisY - Y_MARGIN),
                getValueX(nextX) + (yAxisX - X_MARGIN),
                getValueY(nextY) - (xAxisY - Y_MARGIN)
            );
        }
    }

    //<editor-fold desc="implementation">
    @Override
    protected void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        drawGraphics(g);
    }
    //</editor-fold>
}

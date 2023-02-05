package de.jonas.graphingcalculator.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Mithilfe eines {@link FunctionHandler} lassen sich sowohl alle Funktionswerte in einem bestimmten Bereich, als auch
 * ein bestimmter Funktionswert wiedergeben. Außerdem stellt dieser Handler die Utility-Methode {@code eval} zur
 * Verfügung, welche einen Term ausrechnet.
 */
@NotNull
@RequiredArgsConstructor
public final class FunctionHandler {

    //<editor-fold desc="LOCAL FIELDS">
    /** Die Funktion, für die dieser {@link FunctionHandler} erzeugt wird. */
    @Getter
    @NotNull
    private final String function;
    /** Die Skalierung der x-Achse. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private final int xScaling;
    //</editor-fold>


    /**
     * Gibt alle Funktionswerte in Abständen von 0.001 im Bereich der x-Achsen-Skalierung wieder. In der
     * {@link NavigableMap} sind alle x-Werte den entsprechenden y-Werten zugeordnet.
     *
     * @return Eine {@link NavigableMap}, welche alle Funktionswerte im Bereich der x-Achsen-Skalierung in Abständen von
     *     0.1 beinhaltet.
     */
    @NotNull
    public NavigableMap<Double, Double> getFunctionValues() {
        final NavigableMap<Double, Double> values = new TreeMap<>();

        // calculate function values
        for (double i = -this.xScaling; i < this.xScaling; i = Math.round((i + 0.001) * 1000D) / 1000D) {
            values.put(i, getFunctionValue(i));
        }

        return values;
    }

    /**
     * Gibt alle Funktionswerte der Ableitung dieser Funktion im Bereich der x-Achsen-Skalierung wieder.
     *
     * @return Alle Funktionswerte der Ableitung im Bereich der x-Achsen-Skalierung.
     */
    @NotNull
    public NavigableMap<Double, Double> getDerivationValues() {
        final NavigableMap<Double, Double> derivationValues = new TreeMap<>();
        final NavigableMap<Double, Double> functionValues = getFunctionValues();

        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
            if (functionValue.getValue().isNaN()) continue;

            // get current x
            final double x = functionValue.getKey();

            // check if next or previous entry is preset
            if (functionValues.lowerEntry(x) == null) continue;
            if (functionValues.higherEntry(x) == null) break;

            final Map.Entry<Double, Double> nextEntry = functionValues.higherEntry(x);
            final Map.Entry<Double, Double> previousEntry = functionValues.lowerEntry(x);

            // get next and previous y
            final double nextX = nextEntry.getKey();
            final double nextY = nextEntry.getValue();
            final double previousX = previousEntry.getKey();
            final double previousY = previousEntry.getValue();

            derivationValues.put(x, (nextY - previousY) / (nextX - previousX));
        }

        return derivationValues;
    }

    /**
     * Gibt alle Nullstellen dieser Funktion in Form einer {@link Map} zurück.
     *
     * @return Alle Nullstellen dieser Funktion in Form einer {@link Map}.
     */
    @NotNull
    public Map<Double, Double> getRoots() {
        final Map<Double, Double> roots = new HashMap<>();
        final NavigableMap<Double, Double> functionValues = getFunctionValues();

        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
            if (functionValue.getValue().isNaN()) continue;

            // get current values
            final double x = functionValue.getKey();
            final double y = functionValue.getValue();

            // check if current value is already preset
            if (roots.containsKey(x)) continue;

            // check if next entry is preset
            if (functionValues.higherEntry(x) == null) break;

            // get next entry
            final Map.Entry<Double, Double> nextEntry = functionValues.higherEntry(x);

            // get next values
            final double nextX = nextEntry.getKey();
            final double nextY = nextEntry.getValue();

            // check (+ to +) or (- to -)
            if ((y > 0 && nextY > 0) || (y < 0 && nextY < 0)) continue;

            roots.put(nextX, nextY);
        }

        return roots;
    }

    /**
     * Gibt alle Extremstellen dieser Funktion in Form einer {@link Map} zurück.
     *
     * @return Alle Extremstellen dieser Funktion in Form einer {@link Map}.
     */
    @NotNull
    public Map<Double, Double> getExtremes() {
        final Map<Double, Double> extremes = new HashMap<>();
        final NavigableMap<Double, Double> functionValues = getFunctionValues();

        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
            if (functionValue.getValue().isNaN()) continue;

            // get current values
            final double x = functionValue.getKey();
            final double y = functionValue.getValue();

            // check if next or previous entry is preset
            if (functionValues.lowerEntry(x) == null) continue;
            if (functionValues.higherEntry(x) == null) break;

            // get next and previous y
            final double nextY = functionValues.higherEntry(x).getValue();
            final double previousY = functionValues.lowerEntry(x).getValue();

            if ((previousY < y && nextY < y) || (previousY > y && nextY > y)) extremes.put(x, y);
        }

        return extremes;
    }

    /**
     * Gibt den Funktionswert für einen bestimmten x-Wert zurück.
     *
     * @param x Der x-Wert, dessen Funktionswert wiedergegeben werden soll.
     *
     * @return Der Funktionswert für einen bestimmten x-Wert.
     */
    public double getFunctionValue(final double x) {
        final String function = this.function.replaceAll("x", "(" + x + ")");

        return eval(function);
    }

    /**
     * Gibt die Funktion einer Tangente an einer bestimmten Stelle zurück.
     *
     * @param x Die Stelle, an der die Tangente angelegt werden soll.
     *
     * @return Die Funktion einer Tangente an einer bestimmten Stelle.
     */
    @Nullable
    public String getTangentFunction(final double x) {
        final NavigableMap<Double, Double> functionValues = getFunctionValues();

        if (functionValues.get(x) == null) return null;
        if (functionValues.lowerEntry(x) == null || functionValues.higherEntry(x) == null) return null;

        // get current function value
        final double y = getFunctionValue(x);

        // get next and previous entry
        final Map.Entry<Double, Double> previousEntry = functionValues.lowerEntry(x);
        final Map.Entry<Double, Double> nextEntry = functionValues.higherEntry(x);

        // get next and previous x- and y-coordinates
        final double previousX = previousEntry.getKey();
        final double previousY = previousEntry.getValue();
        final double nextX = nextEntry.getKey();
        final double nextY = nextEntry.getValue();

        // get current pitch
        final double m = Math.round(((nextY - previousY) / (nextX - previousX)) * 100D) / 100D;
        final double b = Math.round((y - m * x) * 100D) / 100D;

        return m + " * x " + (b < 0 ? "- " : "+ ") + Math.abs(b);
    }

    //<editor-fold desc="utility">

    /**
     * Berechnet einen Term, welcher in Form eines Strings übergeben wird und gibt diesen ausgerechnet wieder zurück.
     *
     * @param term Der String der mathematisch berechnet wird.
     *
     * @return Das Ergebnis der Rechnung.
     */
    public static double eval(@NotNull final String term) {
        final String finalTerm = term.replaceAll("e", String.valueOf(Math.E)).replaceAll("π", String.valueOf(Math.PI));

        return new Object() {
            private int pos = -1;
            private int ch;

            private void nextChar() {
                ch = (++pos < finalTerm.length()) ? finalTerm.charAt(pos) : -1;
            }

            private boolean eat(final int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            private double parse() {
                nextChar();
                double x = parseExpression();

                if (pos < finalTerm.length()) {
                    return 0;
                }

                return x;
            }

            private double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) {
                        x += parseTerm();
                    } else if (eat('-')) {
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            private double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) {
                        x *= parseFactor();
                    } else if (eat('/')) {
                        x /= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            private double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(finalTerm.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = finalTerm.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;

                        case "ln":
                            x = Math.log(x);
                            break;

                        case "log":
                            x = Math.log10(x);
                            break;

                        case "sin":
                            x = Math.sin(x);
                            break;

                        case "cos":
                            x = Math.cos(x);
                            break;

                        case "tan":
                            x = Math.tan(x);
                            break;

                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    return 0;
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
    //</editor-fold>

}

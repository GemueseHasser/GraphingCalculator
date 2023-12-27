package de.jonas.graphingcalculator.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final double xScaling;
    //</editor-fold>


    /**
     * Gibt alle Funktionswerte in Abständen von 0.001 im Bereich der x-Achsen-Skalierung wieder. In der
     * {@link NavigableMap} sind alle x-Werte den entsprechenden y-Werten zugeordnet.
     *
     * @return Eine {@link NavigableMap}, welche alle Funktionswerte im Bereich der x-Achsen-Skalierung in Abständen von
     *     0.001 beinhaltet.
     */
    @NotNull
    public NavigableMap<Double, Double> getFunctionValues() {
        final NavigableMap<Double, Double> values = new TreeMap<>();

        // calculate function values
        for (double i = -this.xScaling; i < this.xScaling; i += Math.round(this.xScaling / 10D) / 1000D) {
            final double functionValue = getFunctionValue(Math.round(i * 1000D) / 1000D);

            if (!Double.isFinite(functionValue)) continue;

            values.put(i, functionValue);
        }

        return values;
    }

    /**
     * Gibt alle Wendepunkte dieser Funktion zurück.
     *
     * @return Alle Wendepunkte dieser Funktion.
     */
    @NotNull
    public Map<Double, Double> getTurningPoints() {
        final Map<Double, Double> turningPoints = new HashMap<>();
        final NavigableMap<Double, Double> derivationValues = getDerivationValues(getFunctionValues());

        for (@NotNull final Map.Entry<Double, Double> wsPointEntry : getWSPoints().entrySet()) {
            final double x = wsPointEntry.getKey();
            final double y = wsPointEntry.getValue();
            final double m = Math.round(derivationValues.get(x) * 1000D) / 1000D;

            if (m == 0) continue;

            turningPoints.put(x, y);
        }

        return turningPoints;
    }

    /**
     * Gibt alle Sattelpunkte dieser Funktion zurück.
     *
     * @return Alle Sattelpunkte dieser Funktion.
     */
    @NotNull
    public Map<Double, Double> getSaddlePoints() {
        final Map<Double, Double> saddlePoints = new HashMap<>();
        final NavigableMap<Double, Double> derivationValues = getDerivationValues(getFunctionValues());

        for (@NotNull final Map.Entry<Double, Double> wsPointEntry : getWSPoints().entrySet()) {
            final double x = wsPointEntry.getKey();
            final double y = wsPointEntry.getValue();
            final double m = Math.round(derivationValues.get(x) * 1000D) / 1000D;

            if (m != 0) continue;

            saddlePoints.put(x, y);
        }

        return saddlePoints;
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

        // return function without b if b is 0
        if (b == 0) return m + "x";

        // return function with b
        return m + "x " + (b < 0 ? "- " : "+ ") + Math.abs(b);
    }

    /**
     * Gibt eine Map zurück, die sowohl Wendepunkte als auch Sattelpunkte beinhaltet. Diese Punkte müssen mithilfe ihrer
     * Steigung erst als Wende- oder Sattelpunkt identifiziert werden.
     *
     * @return Eine Map, die sowohl Wendepunkte als auch Sattelpunkte beinhaltet. Diese Punkte müssen mithilfe ihrer
     *     Steigung erst als Wende- oder Sattelpunkt identifiziert werden.
     */
    @NotNull
    private Map<Double, Double> getWSPoints() {
        final Map<Double, Double> wsPoints = new HashMap<>();

        final NavigableMap<Double, Double> functionValues = getFunctionValues();
        final NavigableMap<Double, Double> derivationValues = getDerivationValues(functionValues);

        final Map<Double, Double> derivationExtremes = getExtremes(derivationValues);

        for (@NotNull final Map.Entry<Double, Double> derivationExtremesEntry : derivationExtremes.entrySet()) {
            final double x = derivationExtremesEntry.getKey();
            final double y = getFunctionValue(x);

            wsPoints.put(x, y);
        }

        return wsPoints;
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
        String replacedTerm = term.replaceAll("e", String.valueOf(Math.E)).replaceAll("π", String.valueOf(Math.PI));

        for (int i = 0; i < replacedTerm.length(); i++) {
            if ((i + 1) >= replacedTerm.length()) continue;
            if (replacedTerm.charAt(i + 1) != '(') continue;

            final char currentChar = replacedTerm.charAt(i);

            if ((currentChar < '0' || currentChar > '9') && currentChar != ')') continue;

            replacedTerm = replacedTerm.substring(0, i + 1) + "*" + replacedTerm.substring(i + 1);
        }

        final String finalTerm = replacedTerm;

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

                if (pos < finalTerm.length()) return 0;

                return x;
            }

            private double parseExpression() {
                double x = parseTerm();
                while (true) {
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
                while (true) {
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

    /**
     * Gibt alle Nullstellen dieser Funktion in Form einer {@link Map} zurück.
     *
     * @return Alle Nullstellen dieser Funktion in Form einer {@link Map}.
     */
    @NotNull
    public static Map<Double, Double> getRoots(@NotNull final NavigableMap<Double, Double> functionValues) {
        final Map<Double, Double> roots = new HashMap<>();

        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
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
    public static Map<Double, Double> getExtremes(@NotNull final NavigableMap<Double, Double> functionValues) {
        final Map<Double, Double> extremes = new HashMap<>();

        for (@NotNull final Map.Entry<Double, Double> functionValue : functionValues.entrySet()) {
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
     * Gibt alle Funktionswerte der Ableitung einer Funktion, dessen Funktionswerte bekannt sind wieder.
     *
     * @return Alle Funktionswerte der Ableitung einer Funktion, dessen Funktionswerte bekannt sind.
     */
    @NotNull
    public static NavigableMap<Double, Double> getDerivationValues(
        @NotNull final NavigableMap<Double, Double> function
    ) {
        final NavigableMap<Double, Double> derivationValues = new TreeMap<>();

        for (@NotNull final Map.Entry<Double, Double> functionValue : function.entrySet()) {
            // get current x
            final double x = functionValue.getKey();

            // check if next or previous entry is preset
            if (function.lowerEntry(x) == null) continue;
            if (function.higherEntry(x) == null) break;

            final Map.Entry<Double, Double> nextEntry = function.higherEntry(x);
            final Map.Entry<Double, Double> previousEntry = function.lowerEntry(x);

            // get next and previous y
            final double nextX = nextEntry.getKey();
            final double nextY = nextEntry.getValue();
            final double previousX = previousEntry.getKey();
            final double previousY = previousEntry.getValue();

            final double y = (nextY - previousY) / (nextX - previousX);

            if (!Double.isFinite(y)) continue;

            derivationValues.put(x, y);
        }

        return derivationValues;
    }
    //</editor-fold>

}

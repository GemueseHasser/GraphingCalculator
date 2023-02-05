package de.jonas.graphingcalculator.gui;

import de.jonas.graphingcalculator.handler.FunctionHandler;
import de.jonas.graphingcalculator.object.CalculatorField;
import de.jonas.graphingcalculator.object.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.awt.event.KeyEvent.VK_6;
import static java.awt.event.KeyEvent.VK_7;
import static java.awt.event.KeyEvent.VK_8;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_ADD;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_DIVIDE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_MULTIPLY;
import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_NUMPAD1;
import static java.awt.event.KeyEvent.VK_NUMPAD2;
import static java.awt.event.KeyEvent.VK_NUMPAD3;
import static java.awt.event.KeyEvent.VK_NUMPAD4;
import static java.awt.event.KeyEvent.VK_NUMPAD5;
import static java.awt.event.KeyEvent.VK_NUMPAD6;
import static java.awt.event.KeyEvent.VK_NUMPAD7;
import static java.awt.event.KeyEvent.VK_NUMPAD8;
import static java.awt.event.KeyEvent.VK_NUMPAD9;
import static java.awt.event.KeyEvent.VK_PLUS;
import static java.awt.event.KeyEvent.VK_SEPARATER;
import static java.awt.event.KeyEvent.VK_SUBTRACT;

/**
 * Ein {@link CalculatorGui} ist eine Instanz eines {@link Gui} und stellt eine Oberfläche zur Berechnung einfacher
 * Terme ohne Variable zur Verfügung.
 */
@NotNull
public final class CalculatorGui extends Gui implements ActionListener, KeyListener {

    //<editor-fold desc="CONSTANTS">
    /** Die Breite des Fensters. */
    public static final int WIDTH = 500;
    /** Die Höhe des Fensters. */
    private static final int HEIGHT = 720;
    /** Der Titel des Fensters. */
    private static final String TITLE = "Rechner";
    /** Die Höhe des Textfeldes, welches den aktuellen Term darstellt. */
    private static final int TERM_FIELD_HEIGHT = 100;
    /** Die Schriftart, die standardmäßig in diesem Fenster des Taschenrechners genutzt wird. */
    private static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 28);
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Feld des Taschenrechners, in welchem der eingegebene Term visuell für den Nutzer festgehalten wird. */
    @NotNull
    private final JTextField termField;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link CalculatorGui}. Ein {@link CalculatorGui} ist eine Instanz eines
     * {@link Gui} und stellt eine Oberfläche zur Berechnung einfacher Terme ohne Variable zur Verfügung.
     */
    public CalculatorGui(@NotNull final CalculatorField @NotNull [] calculatorFields) {
        super(TITLE, WIDTH, HEIGHT);

        this.termField = new JTextField();
        this.termField.setBounds(0, 0, WIDTH, TERM_FIELD_HEIGHT);
        this.termField.setEnabled(false);
        this.termField.setDisabledTextColor(Color.BLACK);
        this.termField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 6));
        this.termField.setFont(DEFAULT_FONT);

        super.add(this.termField);

        for (int i = 0; i < calculatorFields.length; i++) {
            if (calculatorFields[i].getName().isEmpty()) continue;

            super.add(getButton(calculatorFields[i], i));
        }

        super.addKeyListener(this);
        super.setVisible(true);
    }
    //</editor-fold>


    /**
     * Führt je nach Text, der übergeben wird, eine andere Aktion aus. Der Text ist der Schlüssel für die Aktion; es
     * wird genau das übergeben, was auf den Buttons steht.
     *
     * @param text Der Text, welcher als Schlüsselwort für die auszuführende Aktion genutzt wird.
     */
    private void performAction(@NotNull final String text) {
        switch (text) {
            case "C":
                this.termField.setText("");
                break;

            case "x^y":
                this.termField.setText(this.termField.getText() + "^");
                break;

            case "⬅":
                if (this.termField.getText().isEmpty()) return;

                this.termField.setText(this.termField.getText().substring(0, this.termField.getText().length() - 1));
                break;

            case "=":
                this.termField.setText(
                    String.valueOf(FunctionHandler.eval(
                        this.termField.getText()
                            .replaceAll("√", "sqrt")
                            .replaceAll("÷", "/")
                            .replaceAll("×", "*")
                            .replaceAll(",", ".")
                    ))
                );
                break;

            default:
                this.termField.setText(this.termField.getText() + text);
                break;
        }
    }

    /**
     * Gibt einen Button zurück, welcher aufgrund seines 'counts' bereits platziert ist und alle Eigenschaften eines
     * Buttons für einen Taschenrechner besitzt.
     *
     * @param field Das entsprechende {@link CalculatorField} auf dessen Grundlage dieser Button erzeugt werden soll.
     * @param count Der aktuelle 'count' des Buttons, wodurch dieser Button an der richtigen Stelle platziert werden
     *              kann.
     *
     * @return Ein Button, welcher aufgrund seines 'counts' bereits platziert ist und alle Eigenschaften eines Buttons
     *     für einen Taschenrechner besitzt.
     */
    @NotNull
    private JButton getButton(
        @NotNull final CalculatorField field,
        @Range(from = 0, to = Integer.MAX_VALUE) final int count
    ) {
        final int x = (count % CalculatorField.FIELDS_PER_ROW) * CalculatorField.DEFAULT_WIDTH;
        final int y = (count / CalculatorField.FIELDS_PER_ROW) * CalculatorField.DEFAULT_HEIGHT + TERM_FIELD_HEIGHT;

        final JButton button = new JButton(field.getName());
        button.setBounds(x, y, CalculatorField.DEFAULT_WIDTH, field.getHeight());
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setBackground(Color.LIGHT_GRAY);
        button.setFont(DEFAULT_FONT);
        button.addActionListener(this);

        return button;
    }

    //<editor-fold desc="implementation">
    @Override
    public void actionPerformed(@NotNull final ActionEvent e) {
        if (!(e.getSource() instanceof JButton)) return;

        final JButton source = (JButton) e.getSource();

        performAction(source.getText());
    }

    @Override
    public void keyPressed(@NotNull final KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_0:
            case VK_NUMPAD0:
                performAction("0");
                break;

            case VK_1:
            case VK_NUMPAD1:
                performAction("1");
                break;

            case VK_2:
            case VK_NUMPAD2:
                performAction("2");
                break;

            case VK_3:
            case VK_NUMPAD3:
                performAction("3");
                break;

            case VK_4:
            case VK_NUMPAD4:
                performAction("4");
                break;

            case VK_5:
            case VK_NUMPAD5:
                performAction("5");
                break;

            case VK_6:
            case VK_NUMPAD6:
                performAction("6");
                break;

            case VK_7:
            case VK_NUMPAD7:
                performAction("7");
                break;

            case VK_8:
            case VK_NUMPAD8:
                performAction("8");
                break;

            case VK_9:
            case VK_NUMPAD9:
                performAction("9");
                break;

            case VK_ENTER:
                performAction("=");
                break;

            case VK_ESCAPE:
                performAction("C");
                break;

            case VK_DELETE:
            case VK_BACK_SPACE:
                performAction("⬅");
                break;

            case VK_PLUS:
            case VK_ADD:
                performAction("+");
                break;

            case VK_MINUS:
            case VK_SUBTRACT:
                performAction("-");
                break;

            case VK_MULTIPLY:
                performAction("×");
                break;

            case VK_DIVIDE:
                performAction("÷");
                break;

            case VK_COMMA:
            case VK_SEPARATER:
                performAction(",");
                break;

            default:
                break;
        }
    }

    @Override
    public void keyTyped(@NotNull final KeyEvent e) {
    }

    @Override
    public void keyReleased(@NotNull final KeyEvent e) {
    }
    //</editor-fold>
}

package de.jonas.graphingcalculator.gui;

import de.jonas.graphingcalculator.handler.FunctionHandler;
import de.jonas.graphingcalculator.object.DrawFunction;
import de.jonas.graphingcalculator.object.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Ein {@link FunctionGui} stellt eine Instanz eines {@link Gui} dar, welches eine grafische Oberfläche darstellt, auf
 * der eine bestimmte Funktion gezeichnet werden kann, mithilfe eines
 * {@link de.jonas.graphingcalculator.object.DrawFunction}.
 */
@NotNull
public final class FunctionGui extends Gui implements MouseListener, MouseMotionListener {

    //<editor-fold desc="CONSTANTS">
    /** Der Titel dieses Fensters. */
    @NotNull
    private static final String TITLE = "Funktion zeichnen";
    /** Die Breite dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int WIDTH = 800;
    /** Die Höhe dieses Fensters. */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private static final int HEIGHT = 850;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Textfeld, in welchem die Skalierung der x-Achse angegeben wird. */
    @NotNull
    private final JTextField xScalingField = new JTextField("10", 10);
    /** Das Textfeld, in welchem die Skalierung der y-Achse angegeben wird. */
    @NotNull
    private final JTextField yScalingField = new JTextField("10", 10);
    /** Die Funktion, welche alle grafischen Inhalte auf das Fenster zeichnet. */
    @Nullable
    private final DrawFunction drawFunction;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link FunctionGui}. Ein {@link FunctionGui} stellt eine Instanz eines
     * {@link Gui} dar, welches eine grafische Oberfläche darstellt, auf der eine bestimmte Funktion gezeichnet werden
     * kann, mithilfe eines {@link de.jonas.graphingcalculator.object.DrawFunction}.
     */
    public FunctionGui() {
        // create frame instance
        super(TITLE, WIDTH, HEIGHT);

        // create function message panel
        final JPanel[] messagePanel = new JPanel[3];

        messagePanel[0] = new JPanel();
        messagePanel[1] = new JPanel();
        messagePanel[2] = new JPanel();

        // add function field
        final JTextField functionField = new JTextField(10);
        messagePanel[0].add(new JLabel("f(x) = "));
        messagePanel[0].add(functionField);

        // add x-scaling field
        messagePanel[1].add(new JLabel("x-Skalierung: "));
        messagePanel[1].add(this.xScalingField);

        // add y-scaling field
        messagePanel[2].add(new JLabel("y-Skalierung: "));
        messagePanel[2].add(this.yScalingField);

        // create dialog
        final int functionDrawOption = JOptionPane.showConfirmDialog(
            null,
            messagePanel,
            "Funktion zeichnen",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (functionDrawOption != JOptionPane.OK_OPTION) {
            this.drawFunction = null;
            return;
        }

        // create new function handler
        final FunctionHandler functionHandler = new FunctionHandler(functionField.getText(), getXScaling());

        // create draw object
        this.drawFunction = new DrawFunction(functionHandler, getXScaling(), getYScaling());
        this.drawFunction.setBounds(0, 0, WIDTH, HEIGHT);
        this.drawFunction.setVisible(true);

        final JButton rootsButton = getOptionButton("Nullstellen berechnen", 0, e -> {
            final JButton source = (JButton) e.getSource();

            if (source.getText().equalsIgnoreCase("Nullstellen berechnen")) {
                this.drawFunction.setEnableRoots(true);
                source.setText("Nullstellen ausblenden");
            } else {
                this.drawFunction.setEnableRoots(false);
                source.setText("Nullstellen berechnen");
            }

            this.drawFunction.repaint();
        });
        final JButton extremesButton = getOptionButton("Extremstellen berechnen", 1, e -> {
            final JButton source = (JButton) e.getSource();

            if (source.getText().equalsIgnoreCase("Extremstellen berechnen")) {
                this.drawFunction.setEnableExtremes(true);
                source.setText("Extremstellen ausblenden");
            } else {
                this.drawFunction.setEnableExtremes(false);
                source.setText("Extremstellen berechnen");
            }

            this.drawFunction.repaint();
        });
        final JButton markPointButton = getOptionButton("Punkt einzeichnen", 2, e -> {
            final String input = JOptionPane.showInputDialog(
                null,
                "X-Wert:",
                "Punkt einzeichnen",
                JOptionPane.PLAIN_MESSAGE
            );

            if (input == null) return;

            try {
                final double x = Double.parseDouble(input.replaceAll(",", "."));

                this.drawFunction.addMarkedPoint(x);
                this.drawFunction.repaint();
            } catch (@NotNull final NumberFormatException ignored) {
            }
        });
        final JButton removeLastMarkedPointButton = getOptionButton("Letzten Punkt entfernen", 3, e -> {
            this.drawFunction.removeLastMarkedPoint();
            this.drawFunction.repaint();
        });
        final JButton tangentButton = getOptionButton("Tangente anlegen", 4, e -> {
            final JButton source = (JButton) e.getSource();

            if (!source.getText().equalsIgnoreCase("Tangente anlegen")) {
                this.drawFunction.setTangentFunction(null);
                this.drawFunction.repaint();

                source.setText("Tangente anlegen");
                return;
            }

            final String input = JOptionPane.showInputDialog(
                null,
                "X-Wert:",
                "Tangente anlegen",
                JOptionPane.PLAIN_MESSAGE
            );

            if (input == null) return;

            try {
                final double x = Double.parseDouble(input.replaceAll(",", "."));

                this.drawFunction.setTangentFunction(functionHandler.getTangentFunction(x));
                this.drawFunction.repaint();

                source.setText("Tangente ausblenden");
            } catch (@NotNull final NumberFormatException ignored) {
            }
        });
        final JButton derivationButton = getOptionButton("Ableitung zeichnen", 5, e -> {
            final JButton source = (JButton) e.getSource();

            if (source.getText().equalsIgnoreCase("Ableitung zeichnen")) {
                this.drawFunction.setEnableDerivation(true);
                source.setText("Ableitung ausblenden");
            } else {
                this.drawFunction.setEnableDerivation(false);
                source.setText("Ableitung zeichnen");
            }

            this.drawFunction.repaint();
        });

        // add components
        super.add(rootsButton);
        super.add(extremesButton);
        super.add(markPointButton);
        super.add(removeLastMarkedPointButton);
        super.add(tangentButton);
        super.add(derivationButton);
        super.add(this.drawFunction);

        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        super.setVisible(true);
    }
    //</editor-fold>


    /**
     * Gibt die Skalierung der x-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers zurück.
     *
     * @return Die Skalierung der x-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers.
     */
    public int getXScaling() {
        try {
            return Integer.parseInt(this.xScalingField.getText());
        } catch (@NotNull final NumberFormatException ignored) {
            return 10;
        }
    }

    /**
     * Gibt die Skalierung der y-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers zurück.
     *
     * @return Die Skalierung der y-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers.
     */
    public int getYScaling() {
        try {
            return Integer.parseInt(this.yScalingField.getText());
        } catch (@NotNull final NumberFormatException ignored) {
            return 10;
        }
    }

    /**
     * Gibt einen positionierten Button mit entsprechendem Text und entsprechendem {@link ActionListener} zurück.
     *
     * @param text           Der Text des Buttons.
     * @param count          Die Nummer des Buttons beginnend bei 0.
     * @param actionListener Der {@link ActionListener}, welcher ausgeführt werden soll, wenn der Button angeklickt
     *                       wird.
     *
     * @return Ein positionierter Button mit entsprechendem Text und entsprechendem {@link ActionListener}.
     */
    @NotNull
    private JButton getOptionButton(
        @NotNull final String text,
        @Range(from = 0, to = Integer.MAX_VALUE) final int count,
        @NotNull final ActionListener actionListener
    ) {
        final JButton button = new JButton(text);
        button.setFocusable(false);
        button.setBounds(WIDTH - 220, 20 + (count * 40), 190, 30);
        button.addActionListener(actionListener);

        return button;
    }

    //<editor-fold desc="implementation">
    @Override
    public void mousePressed(@NotNull final MouseEvent e) {
        assert this.drawFunction != null;
        this.drawFunction.handleMousePressed(e.getX() - 7);
        this.drawFunction.repaint();
    }

    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {
        assert this.drawFunction != null;
        this.drawFunction.handleMouseReleased();
        this.drawFunction.repaint();
    }

    @Override
    public void mouseDragged(@NotNull final MouseEvent e) {
        assert this.drawFunction != null;
        if (this.drawFunction.getMouse() == null) return;

        this.drawFunction.handleMousePressed(e.getX() - 7);
        this.drawFunction.repaint();
    }

    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {
    }

    @Override
    public void mouseEntered(@NotNull final MouseEvent e) {
    }

    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
    }

    @Override
    public void mouseMoved(@NotNull final MouseEvent e) {
    }
    //</editor-fold>
}

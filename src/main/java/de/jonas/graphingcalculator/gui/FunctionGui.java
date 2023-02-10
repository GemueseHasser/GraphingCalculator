package de.jonas.graphingcalculator.gui;

import de.jonas.graphingcalculator.handler.FileHandler;
import de.jonas.graphingcalculator.handler.FunctionHandler;
import de.jonas.graphingcalculator.object.Derivation;
import de.jonas.graphingcalculator.object.DrawFunction;
import de.jonas.graphingcalculator.object.Gui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

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


    //<editor-fold desc="STATIC FIELDS">
    /** Die als letztes gezeichnete Funktion des Nutzers. */
    @NotNull
    private static String lastFunction = "";
    /** Die als letztes verwendete x-Achsen-Skalierung des Nutzers. */
    @NotNull
    private static String lastScalingX = "10";
    /** Die als letztes verwendete y-Achsen-Skalierung des Nutzers. */
    @NotNull
    private static String lastScalingY = "10";
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Textfeld, in welchem die Skalierung der x-Achse angegeben wird. */
    @NotNull
    private final JTextField xScalingField = new JTextField(lastScalingX, 10);
    /** Das Textfeld, in welchem die Skalierung der y-Achse angegeben wird. */
    @NotNull
    private final JTextField yScalingField = new JTextField(lastScalingY, 10);
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
        final JTextField functionField = new JTextField(lastFunction, 10);
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
        final FunctionHandler functionHandler = new FunctionHandler(
            functionField.getText().replaceAll(",", "."),
            getXScaling()
        );

        // set last values
        lastFunction = functionHandler.getFunction();
        lastScalingX = String.valueOf(getXScaling());
        lastScalingY = String.valueOf(getYScaling());

        // create draw object
        this.drawFunction = new DrawFunction(functionHandler, getXScaling(), getYScaling());
        this.drawFunction.setBounds(0, 0, WIDTH, HEIGHT - 21);
        this.drawFunction.setVisible(true);

        // create popup-menu item to show roots
        final JRadioButtonMenuItem showRootsItem = new JRadioButtonMenuItem("Nullstellen anzeigen", false);
        showRootsItem.addChangeListener(e -> {
            if (this.drawFunction.isEnableRoots() == showRootsItem.isSelected()) return;

            this.drawFunction.setEnableRoots(showRootsItem.isSelected());
            this.drawFunction.repaint();
        });

        // create popup-menu item to show extremes
        final JRadioButtonMenuItem showExtremesItem = new JRadioButtonMenuItem("Extremstellen anzeigen", false);
        showExtremesItem.addChangeListener(e -> {
            if (this.drawFunction.isEnableExtremes() == showExtremesItem.isSelected()) return;

            this.drawFunction.setEnableExtremes(showExtremesItem.isSelected());
            this.drawFunction.repaint();
        });

        // create popup-menu item to show turning points
        final JRadioButtonMenuItem showTurningPointsItem = new JRadioButtonMenuItem("Wendepunkte anzeigen", false);
        showTurningPointsItem.addChangeListener(e -> {
            if (this.drawFunction.isEnableTurningPoints() == showTurningPointsItem.isSelected()) return;

            this.drawFunction.setEnableTurningPoints(showTurningPointsItem.isSelected());
            this.drawFunction.repaint();
        });

        // create popup-menu item to show derivation
        final JRadioButtonMenuItem[] derivationItems = new JRadioButtonMenuItem[this.drawFunction.getDerivations().size()];
        for (int i = 0; i < this.drawFunction.getDerivations().size(); i++) {
            derivationItems[i] = new JRadioButtonMenuItem((i + 1) + ". Ableitung anzeigen");

            final int finalI = i;
            derivationItems[i].addChangeListener(e -> {
                final Derivation derivation = this.drawFunction.getDerivations().get(finalI);
                derivation.setDraw(derivationItems[finalI].isSelected());
                this.drawFunction.repaint();
            });
        }

        // create popup-menu item to mark custom points
        final JMenuItem markPointItem = new JMenuItem("Punkt einzeichnen");
        markPointItem.addActionListener(e -> {
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

        // create popup-menu item to remove last point
        final JMenuItem removeLastPointItem = new JMenuItem("Letzten Punkt entfernen");
        removeLastPointItem.addActionListener(e -> {
            this.drawFunction.removeLastMarkedPoint();
            this.drawFunction.repaint();
        });

        // create popup-menu item to draw custom tangent
        final JMenuItem tangentItem = new JMenuItem("Tangente anlegen");
        tangentItem.addActionListener(e -> {
            if (!tangentItem.getText().equalsIgnoreCase("Tangente anlegen")) {
                this.drawFunction.setTangentFunction(null);
                this.drawFunction.repaint();

                tangentItem.setText("Tangente anlegen");
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

                tangentItem.setText("Tangente ausblenden");
            } catch (@NotNull final NumberFormatException ignored) {
            }
        });

        // create button to save current function-gui as png
        final JButton saveToImageButton = new JButton("Als Bild speichern");
        saveToImageButton.setFocusable(false);
        saveToImageButton.setBounds(WIDTH - 220, HEIGHT - 100, 190, 30);
        saveToImageButton.addActionListener(e -> {
            final File file = FileHandler.getSelectedSaveDir();

            if (file == null) return;

            try {
                ImageIO.write(this.drawFunction.getGraphicsAsImage(), "PNG", file);
            } catch (@NotNull final IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // create menu to display points in the menu-bar
        final JMenu pointMenu = new JMenu("Punkte");
        pointMenu.add(showRootsItem);
        pointMenu.add(showExtremesItem);
        pointMenu.add(showTurningPointsItem);

        // create menu to display marks in the menu-bar
        final JMenu markMenu = new JMenu("Markierungen");
        markMenu.add(markPointItem);
        markMenu.add(removeLastPointItem);

        // create menu to display derivations in the menu-bar
        final JMenu derivationMenu = new JMenu("Ableitung");

        for (@NotNull final JRadioButtonMenuItem derivationItem : derivationItems) {
            derivationMenu.add(derivationItem);
        }

        // create menu to display extras in the menu-bar
        final JMenu extraMenu = new JMenu("Extra");
        extraMenu.add(tangentItem);

        // create menu-bar
        final JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(Color.LIGHT_GRAY);

        menuBar.add(pointMenu);
        menuBar.add(markMenu);
        menuBar.add(derivationMenu);
        menuBar.add(extraMenu);

        // set menu-bar
        super.setJMenuBar(menuBar);

        // add components to gui
        super.add(saveToImageButton);
        super.add(this.drawFunction);

        // add listener to gui
        super.addMouseListener(this);
        super.addMouseMotionListener(this);

        // show gui
        super.setVisible(true);
    }
    //</editor-fold>


    /**
     * Gibt die Skalierung der x-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers zurück.
     *
     * @return Die Skalierung der x-Achse unter Berücksichtigung einer falschen Eingebe des Nutzers.
     */
    private int getXScaling() {
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
    private int getYScaling() {
        try {
            return Integer.parseInt(this.yScalingField.getText());
        } catch (@NotNull final NumberFormatException ignored) {
            return 10;
        }
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
        // check if the mouse has clicked before
        assert this.drawFunction != null;
        if (this.drawFunction.getMouse() == null) return;

        // handle mouse-pressed
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

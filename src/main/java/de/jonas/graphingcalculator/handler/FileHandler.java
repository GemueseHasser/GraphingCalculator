package de.jonas.graphingcalculator.handler;

import org.jetbrains.annotations.Nullable;

import javax.swing.JFileChooser;

import java.io.File;

/**
 * Mithilfe des {@link FileHandler} werden alle Interaktionen mit Dateien außerhalb dieses Projekts geregelt.
 */
public final class FileHandler {

    //<editor-fold desc="utility">

    /**
     * Öffnet für den Nutzer ein Fenster, in welchem er einen Pfad auswählen kann, unter dem eine bestimmte Datei
     * gespeichert werden soll.
     *
     * @return Der Pfad, den der Nutzer ausgewählt hat, unter dem eine bestimmte Datei gespeichert werden soll; wenn der
     *     Nutzer keinen Pfad gewählt hat {@code null}.
     */
    @Nullable
    public static File getSelectedSaveDir() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setDialogTitle("Datei speichern unter...");

        final int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }
    //</editor-fold>

}

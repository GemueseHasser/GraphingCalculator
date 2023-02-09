package de.jonas.graphingcalculator.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.NavigableMap;

/**
 * Eine {@link Derivation Ableitung} besteht aus einigen Funktionswerten der Ableitung und einem Zustand, ob sie
 * gezeichnet werden soll oder nicht.
 */
@NotNull
@RequiredArgsConstructor
public final class Derivation {

    //<editor-fold desc="LOCAL FIELDS">
    /** Die Funktionswerte dieser Ableitung. */
    @Getter
    @NotNull
    private final NavigableMap<Double, Double> derivationValues;
    /** Der Zustand, ob diese Ableitung angezeigt werden soll oder nicht. */
    @Getter
    @Setter
    private boolean draw;
    //</editor-fold>

}

package de.jonas.graphingcalculator.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Ein {@link Point Punkt} besteht aus einer x- und einer y-Koordinate, wodurch dieser Punkt definiert wird.
 */
@NotNull
@RequiredArgsConstructor
public final class Point {

    //<editor-fold desc="LOCAL FIELDS">
    /** Die x-Koordinate dieses Punkts. */
    @Getter
    private final double x;
    /** Die y-Koordinate dieses Punkts. */
    @Getter
    private final double y;
    //</editor-fold>

}

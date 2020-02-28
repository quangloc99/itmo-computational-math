package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;

public class MathUtils {
    public static double crossProduct(@NotNull Vector lhs, @NotNull Vector rhs)
        throws MathException
    {
        if (lhs.getSize() != rhs.getSize()) {
            throw new MathException(String.format("%s and %s need to have the same size", lhs, rhs));
        }
        double result = 0;
        for (int i = 0; i < lhs.getSize(); ++i) {
            result += lhs.get(i) * rhs.get(i);
        }
        return result;
    }

    public static double partialCrossProduct(@NotNull Vector lhs, @NotNull Vector rhs, int from, int to)
            throws MathException
    {
        if (lhs.getSize() < to) {
            throw new MathException(String.format("Size of %s needs to be at least %d", lhs, to));
        }
        if (rhs.getSize() < to) {
            throw new MathException(String.format("Size of %s needs to be at least %d", rhs, to));
        }

        double result = 0;
        for (int i = from; i < to; ++i) {
            result += lhs.get(i) * rhs.get(i);
        }
        return result;
    }
}

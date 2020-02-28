package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;

public class MathUtils {
    public static double EPS = 1e-8;

    public static boolean areAlmostEqual(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    /**
     * Finds the next lexical greater permutation of the input.
     * Small note: I wanted to use Iterator for this one, but doing so is actually more complication and
     * I need to care about the mutability stuff.
     *
     * @param permutation the input.
     * @return true if there is a new permutation, and false otherwise.
     */
    public static boolean nextPermutation(int[] permutation) {
        int firstDecreasingPos = permutation.length - 1;
        while (firstDecreasingPos > 0 && permutation[firstDecreasingPos - 1] > permutation[firstDecreasingPos]) {
            --firstDecreasingPos;
        }

        if (firstDecreasingPos == 0) return false;
        int firstSmaller = firstDecreasingPos + 1;
        while (firstSmaller < permutation.length && permutation[firstSmaller] > permutation[firstDecreasingPos]) {
            ++firstSmaller;
        }
        int temp = permutation[firstDecreasingPos];
        permutation[firstDecreasingPos] = permutation[firstSmaller - 1];
        permutation[firstSmaller - 1] = temp;
        for (int l = firstDecreasingPos + 1, r = permutation.length - 1; l < r; ++l, --r) {
            temp = permutation[l];
            permutation[l] = permutation[r];
            permutation[r] = temp;
        }
        return true;
    }

    public static double dotProduct(@NotNull Vector lhs, @NotNull Vector rhs)
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

    public static double partialDotProduct(@NotNull Vector lhs, @NotNull Vector rhs, int from, int to)
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

    public static Vector mul(@NotNull Matrix lhs, @NotNull Vector rhs)
            throws MathException
    {
        if (lhs.getColumnCount() != rhs.getSize()) {
            throw new MathException(String.format("%s must have column count equals to %s's size", lhs, rhs));
        }
        Vector result = new Vector(rhs.getSize());
        for (int r = 0; r < lhs.getRowCount(); ++r) {
            result.set(r, dotProduct(lhs.getRow(r), rhs));
        }

        return result;
    }
}

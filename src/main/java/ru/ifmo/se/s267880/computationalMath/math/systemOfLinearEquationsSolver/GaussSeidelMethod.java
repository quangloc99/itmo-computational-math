package ru.ifmo.se.s267880.computationalMath.math.systemOfLinearEquationsSolver;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.se.s267880.computationalMath.math.MathUtils;
import ru.ifmo.se.s267880.computationalMath.math.Matrix;
import ru.ifmo.se.s267880.computationalMath.math.Vector;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;

import javax.naming.LimitExceededException;
import java.util.Iterator;

public class GaussSeidelMethod {
    private Matrix coefficients;
    private Matrix shuffledCoefficients;

    private Vector constantTerms;
    private double accuracy;

    private int[] shuffleOrder;
    private int[] reverseShuffleOrder;

    public GaussSeidelMethod(@NotNull Matrix coefficients, @NotNull  Vector constantTerms)
            throws MathException
    {
        this(coefficients, constantTerms, MathUtils.EPS);
    }

    public GaussSeidelMethod(@NotNull Matrix coefficients, @NotNull  Vector constantTerms, double accuracy)
            throws MathException
    {
        assert(coefficients.getRowCount() == constantTerms.getSize());
        this.coefficients = coefficients.copy();
        this.constantTerms = constantTerms.copy();
        this.accuracy = accuracy;
        this.shuffleOrder = findMaxDiagonalShuffleOrder(coefficients);
        this.shuffledCoefficients = shuffleWithOrder(coefficients, this.shuffleOrder);
        if (!this.shuffledCoefficients.isDiagonallyDominant(true)) {
            throw new MathException(
                    "Could not find any permutation of column to make the coefficients matrix diagonally dominant"
            );
        }
        this.reverseShuffleOrder = getReverseOrder(this.shuffleOrder);
    }

    public Iterable<Vector> solve(Vector initialX) {
        return () -> new Iterator<Vector>() {
            private Vector currentX = initialX;
            private double maxError = Double.POSITIVE_INFINITY;

            @Override
            public boolean hasNext() {
                return maxError > accuracy;
            }

            @Override
            public Vector next() {
                Vector previousX = currentX.copy();
                try {
                    for (int i = 0; i < getUnknownCount(); ++i) {
                        double previousNewProduct = MathUtils.partialDotProduct(currentX, shuffledCoefficients.getRow(i), 0, i);
                        double nextOldProduct = MathUtils.partialDotProduct(currentX, shuffledCoefficients.getRow(i),
                                i + 1, getUnknownCount());
                        currentX.set(i, (constantTerms.get(i) - previousNewProduct - nextOldProduct) / shuffledCoefficients.get(i, i));
                    }
                    maxError = previousX.subtract(currentX).getMaximumNorm();
                    return shuffleWithOrder(currentX, reverseShuffleOrder);
                } catch (MathException e) {
                    // Currently every MathException here are just size checking, which are done beforehand.
                    // Therefore if there is such exception, then definitely there is something wrong.
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public Vector getErrorColumn(Vector x) throws MathException {
        return MathUtils.mul(coefficients, x).subtract(constantTerms);
    }

    private Vector shuffleWithOrder(Vector vector, int[] order) {
        assert(vector.getSize() == order.length);
        Vector result = new Vector(vector.getSize());
        for (int i = 0; i < order.length; ++i) {
            result.set(i, vector.get(order[i]));
        }
        return result;
    }

    private Matrix shuffleWithOrder(Matrix matrix, int[] columnOrder) {
        assert(matrix.getColumnCount() == columnOrder.length);
        Matrix result = new Matrix(matrix.getRowCount(), matrix.getColumnCount());
        for (int r = 0; r < matrix.getRowCount(); ++r) {
            for (int c = 0; c < matrix.getColumnCount(); ++c) {
                result.set(r, c, matrix.get(r, columnOrder[c]));
            }
        }
        return result;
    }

    private int[] findMaxDiagonalShuffleOrder(Matrix matrix) {
        int[] shuffleOrder = new int[getUnknownCount()];
        boolean[] picked = new boolean[getUnknownCount()];
        for (int r = 0; r < getUnknownCount(); ++r) {
            int maxColumn = -1;
            for (int c = 0; c < getUnknownCount(); ++c) {
                if (picked[c]) continue;
                if (maxColumn == -1 || Math.abs(matrix.get(r, maxColumn)) < Math.abs(matrix.get(r, c))) {
                    maxColumn = c;
                }
            }
            assert(maxColumn != -1);
            picked[maxColumn] = true;
            shuffleOrder[r] = maxColumn;
        }
        return shuffleOrder;
    }

    private int[] getReverseOrder(int[] order) {
        int[] result = new int[order.length];
        for (int i = 0; i < order.length; ++i) {
            result[order[i]] = i;
        }
        return result;
    }

    public int getUnknownCount() {
        return coefficients.getColumnCount();
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}

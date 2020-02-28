package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;

import static ru.ifmo.se.s267880.computationalMath.math.MathUtils.areAlmostEqual;

public class Matrix {
    private Vector[] data;

    public Matrix(int rowCount, int columnCount) {
        assert(rowCount > 0);
        assert(columnCount > 0);
        data = new Vector[rowCount];
        for (int r = 0; r < rowCount; ++r) {
            data[r] = new Vector(columnCount);
        }
    }

    public Matrix(Vector ...data) {
        this.data = new Vector[data.length];
        for (int r = 0; r < data.length; ++r) {
            this.data[r] = data[r].copy();
        }
    }

    public Matrix(@NotNull Matrix other) {
        data = new Vector[other.getRowCount()];
        for (int r = 0; r < getRowCount(); ++r) {
            data[r] = other.data[r].copy();
        }
    }

    public Matrix copy() {
        return new Matrix(this);
    }

    // TODO: add basic operations like "add", "subtract", "scale", ...
    //       I currently don't add them since the 1st lab does not require.

    public boolean isDiagonallyDominant() {
        return isDiagonallyDominant(false);
    }

    public boolean isDiagonallyDominant(boolean strict) {
        for (int r = 0; r < getRowCount(); ++r) {
            double sum = 0;
            for (int c = 0; c < getColumnCount(); ++c) {
                sum += Math.abs(get(r, c));
            }
            sum -= 2 * Math.abs(get(r, r));
            if (sum > 0 || (strict && areAlmostEqual(sum,0))) {
                return false;
            }
        }
        return true;
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return data[0].getSize();
    }

    public void set(int row, int col, double value) {
        data[row].set(col, value);
    }

    public double get(int row, int col) {
        return data[row].get(col);
    }

    public Vector getRow(int row) {
        return data[row];
    }

    @Override
    public String toString() {
        return String.format("Matrix(size = %d x %d)", getRowCount(), getColumnCount());
    }
}

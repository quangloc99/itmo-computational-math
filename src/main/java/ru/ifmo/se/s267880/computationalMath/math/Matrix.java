package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;

import ru.ifmo.se.s267880.computationalMath.math.exceptions.RequiredSameDimensionsException;

public class Matrix {
    private double[][] data;

    public Matrix(int rowCount, int columnCount) {
        assert(rowCount > 0);
        assert(columnCount > 0);
        data = new double[rowCount][columnCount];
    }

    public Matrix(@NotNull Matrix other) {
        this(other.getRowCount(), other.getColumnCount());
        for (int r = 0; r < getRowCount(); ++r) {
            System.arraycopy(other.data[r], 0, data[r], 0, getColumnCount());
        }
    }

    public Matrix copy() {
        return new Matrix(this);
    }

    public Matrix add(@NotNull  Matrix other) throws RequiredSameDimensionsException {
        requiresSameDimensions(other);
        for (int r = 0; r < getRowCount(); ++r) {
            for (int c = 0; c < getColumnCount(); ++c) {
                data[r][c] += other.data[r][c];
            }
        }
        return this;
    }

    public Matrix subtract(@NotNull  Matrix other) throws RequiredSameDimensionsException {
        requiresSameDimensions(other);
        for (int r = 0; r < getRowCount(); ++r) {
            for (int c = 0; c < getColumnCount(); ++c) {
                data[r][c] -= other.data[r][c];
            }
        }
        return this;
    }

    public Matrix scale(double factor) {
        for (int r = 0; r < getRowCount(); ++r) {
            for (int c = 0; c < getColumnCount(); ++c) {
                data[r][c] *= factor;
            }
        }
        return this;
    }

    private void requiresSameDimensions(Matrix other) throws RequiredSameDimensionsException {
        if (getRowCount() != other.getRowCount() || getColumnCount() != other.getColumnCount()) {
            throw new RequiredSameDimensionsException(this, other);
        }
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return data[0].length;
    }

    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    public double get(int row, int col) {
        return data[row][col];
    }
}

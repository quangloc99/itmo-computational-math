package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class Vector {
    private double[] data;

    public Vector(int size) {
        data = new double[size];
    }

    public Vector(double ...data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    public Vector(@NotNull Vector other) {
        this(other.getSize());
        System.arraycopy(other.data, 0, data, 0, data.length);
    }

    public Vector copy() {
        return new Vector(this);
    }

    public Vector subtract(@NotNull  Vector other) throws MathException  {
        if (getSize() != other.getSize()) {
            throw new MathException(String.format("%s must has same size as %s", other, this));
        }
        for (int i = 0; i < getSize(); ++i) {
            data[i] -= other.data[i];
        }
        return this;
    }

    // TODO: basic some more operations like add, scale
    //       I currently don't add them since the 1st lab does not require.

    public double getMaximumNorm() {
        double result = 0;
        for (int i = 0; i < getSize(); ++i) {
            result = Math.max(result, Math.abs(data[i]));
        }
        return result;
    }

    public int getSize() {
        return data.length;
    }

    public void set(int index, double value) {
        data[index] = value;
    }

    public double get(int index) {
        return data[index];
    }

    @Override
    public String toString() {
        return String.format("Vector(size = %d)", getSize());
    }

    public String getDataAsString() {
        return Arrays.stream(data).mapToObj(it -> String.format("%11.5f", it)).collect(Collectors.joining(", "));
    }

    public DoubleStream streamData() {
        return Arrays.stream(data);
    }
}

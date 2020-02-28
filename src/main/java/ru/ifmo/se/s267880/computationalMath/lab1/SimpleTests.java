package ru.ifmo.se.s267880.computationalMath.lab1;

import ru.ifmo.se.s267880.computationalMath.math.Matrix;
import ru.ifmo.se.s267880.computationalMath.math.Vector;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;
import ru.ifmo.se.s267880.computationalMath.math.systemOfLinearEquationsSolver.GuassSeidelMethod;

import javax.naming.LimitExceededException;
import java.util.function.DoubleToIntFunction;

public class SimpleTests {
    static void solveAndPrint(Matrix coef, Vector constants, Vector initX)
            throws MathException, LimitExceededException
    {
        int i = 0;
        for (Vector x: new GuassSeidelMethod(coef, constants, 1e-10).solve(initX)) {
            System.out.printf("Iteration %d: %s\n", i++, x.getDataAsString());
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Test 1");
            solveAndPrint(new Matrix(
                    new Vector(16, 3),
                    new Vector(7, -11)
            ), new Vector(11, 13), new Vector(1, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Test 2");
            solveAndPrint(new Matrix(
                    new Vector(10, -1, 2, 0),
                    new Vector(-1, 11, -1, 3),
                    new Vector(2, -1, 10, -1),
                    new Vector(0, 3, -1, 8)
            ), new Vector(6, 25, -11, 15), new Vector(0, 0, 0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Test 3");
            // Swap the columns of above test with order: 2 4 1 3
            solveAndPrint(new Matrix(
                    new Vector(-1, 0, 10, 2),
                    new Vector(11, 3, -1, -1),
                    new Vector(-1, -1, 2, 10),
                    new Vector(3, 8, 0, -1)
            ), new Vector(6, 25, -11, 15), new Vector(0, 0, 0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Test 4");
            solveAndPrint(new Matrix(
                    new Vector(2, 3),
                    new Vector(5, 7)
            ), new Vector(11, 13), new Vector(1, 1));
        } catch (MathException e) {
            e.printStackTrace();
        } catch (LimitExceededException e) {
            System.out.println(e);;
        }
    }
}

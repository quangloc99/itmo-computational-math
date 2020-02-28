package ru.ifmo.se.s267880.computationalMath.lab1;

import ru.ifmo.se.s267880.computationalMath.math.MathUtils;
import ru.ifmo.se.s267880.computationalMath.math.Matrix;
import ru.ifmo.se.s267880.computationalMath.math.Vector;
import ru.ifmo.se.s267880.computationalMath.math.exceptions.MathException;
import ru.ifmo.se.s267880.computationalMath.math.systemOfLinearEquationsSolver.GuassSeidelMethod;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    public static final String delimiterRegex = "[\\s;,]+";
    public static void main(String[] args) {
        Scanner argInputProvider = new Scanner(String.join(" ", args)).useDelimiter(delimiterRegex);
        new App(argInputProvider);
    }

    public App(Scanner ...inputProviders) {
        userInputProviderQueue.addAll(Arrays.asList(inputProviders));
        initCommands();
        setN(3);
        loop();
    }

    // Datas for interactive apps
    boolean usingInteractive = false;
    Map<String, Command> commands = new LinkedHashMap<>();
    Deque<Scanner> userInputProviderQueue = new LinkedList<>();

    // Datas for solving system of linear equations.
    boolean printIteration = false;
    int n;
    double accuracy = MathUtils.EPS;
    int shuffleLimit = -1;
    Matrix coefficientMatrix;
    Vector constantTerms;
    Vector solution;
    GuassSeidelMethod solver;

    void setN(int n) {
        this.n = n;
        coefficientMatrix = new Matrix(n, n);
        constantTerms = new Vector(n);
        solution = new Vector(n);
        solver = null;
    }

    void loop() {
        while (!userInputProviderQueue.isEmpty()) {
            if (!getCurrentInputProvider().hasNext()) {
                userInputProviderQueue.removeLast();
                continue;
            }
            String commandName = getCurrentInputProvider().next();
            if (commandName.charAt(0) != '-') {
                System.err.printf("Error: Expected commandName (begin with '-'), but got \"%s\"\n", commandName);
                continue;
            }

            commandName = commandName.substring(1);
            if (!commands.containsKey(commandName)) {
                System.err.printf("Error: There is no command with name \"%s\"\n", commandName);
                continue;
            }
            try {
                commands.get(commandName).process();
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    Scanner getCurrentInputProvider() {
        return userInputProviderQueue.getLast();
    }

    void initCommands() {
        commands.put("help", new Command("Print this message", () -> {
            commands.forEach((commandName, command) -> {
                System.out.printf("\t-%-40s - %s\n", commandName + " " + command.getArgumentDescription(), command.getUsage());
            });
        }));
        commands.put("i", new Command("Enter interactive mode.", () -> {
            if (usingInteractive) {
                throw new Exception("You are already in the interactive mode.");
            }
            userInputProviderQueue.addFirst(new Scanner(System.in).useDelimiter(delimiterRegex));
            usingInteractive = true;
        }));
        commands.put("hello-world", new Command("Print hello world, just for testing :))", () ->
                System.out.println("Hello world"))
        );
        commands.put("exit", new Command("Wonder what does this command do???", () -> System.exit(0)));

        commands.put("set-n", new Command(
                "Set the number of rows/columns/unknowns. Note that the data will be ERASED. Default is 3.",
                "{int}",
                () -> {
                    try {
                        String val = getCurrentInputProvider().next();
                        setN(Integer.parseInt(val));
                    } catch (NumberFormatException e) {
                        throw new Exception("Expecting an integer.", e);
                    }
                }
        ));

        commands.put("summary", new Command("Print summary (the current state of the program)", () -> {
            System.out.printf("\tNumber of rows/columns/unknowns: %d\n", n);
            System.out.printf("\tAccuracy: %f\n", accuracy);
            System.out.printf("\tShuffle limit: %s\n", shuffleLimit == -1 ? "Infinity" : shuffleLimit);
            System.out.println("\tSystem of equations:");
            for (int r = 0; r < n; ++r) {
                System.out.print("\t\t");
                PrimitiveIterator.OfInt columnIter = IntStream.range(1, n + 1).iterator();
                System.out.print(coefficientMatrix.getRow(r).streamData()
                        .mapToObj(it -> String.format("%.3fx%d", it, columnIter.next()))
                        .collect(Collectors.joining(" + "))
                );

                System.out.printf(" = %.3f\n", constantTerms.get(r));
            }

            System.out.println("\tCurrent solution: ");
            System.out.println("\t\t" + solution.getDataAsString());
            System.out.println("\tError column:");
            if (solver == null) {
                System.out.println("\t\t" + "Unavailable (use -solve command)");
            } else {
                System.out.println("\t\t" + solver.getErrorColumn(solution).getDataAsString());
            }
        }));

        commands.put("set-cell", new Command(
                "Set the cell of coefficient matrix at r, c (0-indexed) to be value. Value can be real number",
                "{r} {c} {value}",
                () -> {
                    try {
                        String rString = getCurrentInputProvider().next();
                        String cString = getCurrentInputProvider().next();
                        String valueString = getCurrentInputProvider().next();
                        coefficientMatrix.set(Integer.parseInt(rString), Integer.parseInt(cString), Double.parseDouble(valueString));
                        solver = null;
                    } catch (NumberFormatException e) {
                        throw new Exception("Malformed number");
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound");
                    }
                }
        ));

        commands.put("set-row", new Command(
                "Set all cell of the i-th row. Exactly n + 1 numbers will be read", "{i} {double} {double} ...",
                () -> {
                    try {
                        String iString = getCurrentInputProvider().next();
                        String[] args = new String[n];
                        for (int c = 0; c < n; ++c) {
                            args[c] = getCurrentInputProvider().next();
                        }
                        int i = Integer.parseInt(iString);
                        for (int c = 0; c < n; ++c) {
                            coefficientMatrix.set(i, c, Double.parseDouble(args[c]));
                        }
                    } catch (NumberFormatException e) {
                        throw new Exception("Expected numbers");
                    }
                }
        ));

        commands.put("set-constant-term", new Command(
                "Set the i-th constant term (free member) to be value. Used for setting the initial value for iteration.", "{i} {value}",
                () -> {
                    try {
                        String iString = getCurrentInputProvider().next();
                        String valueString = getCurrentInputProvider().next();
                        constantTerms.set(Integer.parseInt(iString), Double.parseDouble(valueString));
                        solver = null;
                    } catch (NumberFormatException e) {
                        throw new Exception("Malformed number");
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound");
                    }
                }
        ));

        commands.put("set-all-constants", new Command(
                "Set all constant terms (free members). Exactly n numbers will be read", "{double} {double} ...",
                () -> {
                    try {
                        String[] args = new String[n];
                        for (int i = 0; i < n; ++i) {
                            args[i] = getCurrentInputProvider().next();
                        }
                        for (int i = 0; i < n; ++i) {
                            constantTerms.set(i, Double.parseDouble(args[i]));
                        }
                    } catch (NumberFormatException e) {
                        throw new Exception("Expected numbers");
                    }
                }
        ));

        commands.put("set-solution", new Command(
            "Set the i-th member of solution to be value", "{i} {value}",
                () -> {
                    try {
                        String iString = getCurrentInputProvider().next();
                        String valueString = getCurrentInputProvider().next();
                        solution.set(Integer.parseInt(iString), Double.parseDouble(valueString));
                        solver = null;
                    } catch (NumberFormatException e) {
                        throw new Exception("Malformed number");
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound");
                    }
                }
        ));

        commands.put("randomize-solution", new Command("Set randomized solution.", () -> {
            for (int i = 0; i < n; ++i) {
                solution.set(i, Math.random() * 1000);
            }
        }));

        commands.put("set-all-solutions", new Command("Set all number in the solution. Exactly n numbers after this will be read",
                "{double} {double} ...",
                () -> {
                    try {
                        String[] args = new String[n];
                        for (int i = 0; i < n; ++i) {
                            args[i] = getCurrentInputProvider().next();
                        }
                        for (int i = 0; i < n; ++i) {
                            solution.set(i, Double.parseDouble(args[i]));
                        }
                    } catch (NumberFormatException e) {
                        throw new Exception("Expected numbers");
                    }
                }
        ));
    }
}

package ru.ifmo.se.s267880.computationalMath.lab1;

import ru.ifmo.se.s267880.computationalMath.math.MathUtils;
import ru.ifmo.se.s267880.computationalMath.math.Matrix;
import ru.ifmo.se.s267880.computationalMath.math.Vector;
import ru.ifmo.se.s267880.computationalMath.math.systemOfLinearEquationsSolver.GaussSeidelMethod;

import javax.naming.LimitExceededException;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    public static final String delimiterRegex = "[\\s;,]+";
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Use the flag -help.");
        }
        Scanner argInputProvider = new Scanner(String.join(" ", args)).useDelimiter(delimiterRegex);
        new App(argInputProvider);
    }

    public App(Scanner ...inputProviders) {
        userInputProviderQueue.addAll(Arrays.asList(inputProviders));
        initCommands();
        setN(3);
        loop();
        afterLoop();
    }

    // Data for interactive apps
    boolean usingInteractive = false;
    Map<String, Command> commands = new LinkedHashMap<>();
    Deque<Scanner> userInputProviderQueue = new LinkedList<>();

    // Data for solving system of linear equations.
    boolean printIteration = false;
    int n;
    double accuracy = MathUtils.EPS;
    int shuffleLimit = -1;
    Matrix coefficientMatrix;
    Vector constantTerms;
    Vector solution;
    GaussSeidelMethod solver;
    int numberOfIterations = 0;

    void setN(int n) {
        if (n <= 0) {
            throw new RuntimeException("n must be positive");
        }
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

    void afterLoop() {
        try {
            commands.get("solve").process();
            commands.get("result-summary").process();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    Scanner getCurrentInputProvider() {
        return userInputProviderQueue.getLast();
    }

    int readInt() throws Exception {
        try {
            String num = getCurrentInputProvider().next();
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            throw new Exception("Expecting number");
        }
    }

    double readDouble() throws Exception {
        try {
            String num = getCurrentInputProvider().next();
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            throw new Exception("Expecting number");
        }
    }

    void initCommands() {
        commands.put("help", new Command("Print this message", () -> {
            System.out.println("# About");
            System.out.println(
                    "This is a program that calculate the solution for system of linear equations using\n" +
                    "Guass-Seidel method. It is DESIGNED to use with command line, but you can also\n" +
                    "use it with input from your keyboard using -i flag (interactive mode), or use with file, \n" +
                    "using -run-file command. Basically, flag and command for interactive mode in this program\n" +
                    "are the same. For example, if you use flag `-set-cell 2 2 3.5`, then you can do the same thing\n" +
                    "in the terminal with the following:\n" +
                    "\t\t-set-cell 2 2 3.5\n" +
                    "For nicer syntax, this program also treat commans (',') and semi-colon (';') as white space. So\n" +
                    "the above can also be done with:\n" +
                    "\t\t-set-cell 2,3; 3.5\n" +
                    "Or even nicer with longer command. For example if you want to change the content of the 2nd row\n"+
                    "of the coefficient matrix to 1, 2, 3, 4, 5, 6 respectively, then you can do it with:\n" +
                    "\t\t-set-row 2;\n" +
                    "\t\t1, 2, 3, 4, 5, 6" +
                    "\n" +
                    "For running the file, you can write the list of commands into a file the same way as if you write it within\n" +
                    "the terminal.\n" +
                    "\n"+
                    "Also small note that all index here start from zero"
            );

            System.out.println("# Commands/flags list:");
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
            System.out.println("You are now in interactive mode. Use command -help for more details.");
        }));
        commands.put("hello-world", new Command("Print hello world, just for testing :))", () ->
                System.out.println("Hello world"))
        );
        commands.put("exit", new Command("Wonder what does this command do???", () -> userInputProviderQueue.removeLast()));

        commands.put("summary", new Command("Print summary (the current state of the program)", () -> {
            System.out.printf("\tNumber of rows/columns/unknowns: %d\n", n);
            System.out.printf("\tAccuracy: %.20f\n", accuracy);
            System.out.printf("\tShuffle limit: %s\n", shuffleLimit == -1 ? "Infinity" : shuffleLimit);
            System.out.printf("\tPrinting iteration: %s\n", printIteration ? "Yes" : "No");
            System.out.println("\tSystem of equations:");
            for (int r = 0; r < n; ++r) {
                System.out.print("\t\t");
                PrimitiveIterator.OfInt columnIter = IntStream.range(1, n + 1).iterator();
                System.out.print(coefficientMatrix.getRow(r).streamData()
                        .mapToObj(it -> String.format("%11.5fx%d", it, columnIter.next()))
                        .collect(Collectors.joining(" + "))
                );

                System.out.printf(" = %11.5f\n", constantTerms.get(r));
            }
            commands.get("result-summary").process();
        }));

        commands.put("result-summary", new Command("Print only summary about the result", () -> {
            System.out.println("\tCurrent solution: ");
            System.out.println("\t\t" + solution.getDataAsString());
            if (solver == null) {
                System.out.println("\tError column:");
                System.out.println("\t\t" + "Unavailable (use -solve command)");
            } else {
                System.out.println("\tError column:");
                System.out.println("\t\t" + solver.getErrorColumn(solution).getDataAsString());
                System.out.printf("\tNumber of iterations: %d\n", numberOfIterations);
            }
        }));

        commands.put("set-n", new Command(
                "Set the number of rows/columns/unknowns. Note that the data will be ERASED. Default is 3.",
                "{int}",
                () -> setN(readInt())
        ));

        commands.put("set-cell", new Command(
                "Set the cell of coefficient matrix at r, c to be value. Value can be real number",
                "{r} {c} {value}",
                () -> {
                    coefficientMatrix.set(readInt(), readInt(), readDouble());
                    solver = null;
                }
        ));

        commands.put("set-row", new Command(
                "Set all cell of the i-th row. Exactly n + 1 numbers will be read", "{i} {double} {double} ...",
                () -> {
                    try {
                        solver = null;
                        int r = readInt();
                        for (int c = 0; c < n; ++c) {
                            coefficientMatrix.set(r, c, readDouble());
                        }
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound", e);
                    }
                }
        ));

        commands.put("set-constant-term", new Command(
                "Set the i-th constant term (free member) to be value. Used for setting the initial value for iteration.", "{i} {value}",
                () -> {
                    try {
                        solver = null;
                        constantTerms.set(readInt(), readDouble());
                        solver = null;
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound");
                    }
                }
        ));

        commands.put("set-all-constants", new Command(
                "Set all constant terms (free members). Exactly n numbers will be read", "{double} {double} ...",
                () -> {
                    solver = null;
                    for (int i = 0; i < n; ++i) {
                        constantTerms.set(i, readDouble());
                    }
                }
        ));

        commands.put("set-solution", new Command(
            "Set the i-th member of solution to be value", "{i} {value}",
                () -> {
                    try {
                        solver = null;
                        solution.set(readInt(), readDouble());
                        solver = null;
                    } catch (IndexOutOfBoundsException e) {
                        throw new Exception("Index out of bound");
                    }
                }
        ));

        commands.put("randomize-solution", new Command("Set randomized solution.", () -> {
            solver = null;
            for (int i = 0; i < n; ++i) {
                solution.set(i, Math.random() * 1000);
            }
        }));

        commands.put("set-all-solutions", new Command("Set all number in the solution. Exactly n numbers after this will be read",
                "{double} {double} ...",
                () -> {
                    solver = null;
                    for (int i = 0; i < n; ++i) {
                        solution.set(i, readDouble());
                    }
                }
        ));

        commands.put("set-accuracy", new Command("Set accuracy", "{double}", () -> {
            solver = null;
            accuracy = Math.abs(readDouble());
        }));

        commands.put("set-shuffle-limit", new Command("Set the shuffle limit. Non-positive number for Infinity", "{int}", () -> {
            solver = null;
            shuffleLimit = readInt();
            if (shuffleLimit <= 0) shuffleLimit = -1;
        }));

        commands.put("toggle-printing-iteration", new Command("Toggle the printing iteration options", () -> {
            printIteration = !printIteration;
        }));

        commands.put("solve", new Command("Solve the equation. This is used in interactive mode. However, this command is also automatically called after the you are done with interactive mode.", () -> {
            try {
                solver = new GaussSeidelMethod(coefficientMatrix, constantTerms, accuracy, shuffleLimit);
                numberOfIterations = 0;
                for (Vector newSol: solver.solve(solution)) {
                    solution = newSol;
                    ++numberOfIterations;
                    if (!printIteration) continue;
                    System.out.printf("Iteration %d:\t%s\n", numberOfIterations, solution.getDataAsString());
                }
            } catch (LimitExceededException e) {
                throw new Exception("Cannot find a permutation for diagonally dominant matrix after " + shuffleLimit + " times shuffling. " +
                        "You can set the shuffle limit to be higher if you can wait :)))");
            }
        }));

        commands.put("run-file", new Command("Run all commands written the file. Note that due to simplicity, the file-name will be trim", "{file-name}", () -> {
            String fileName = getCurrentInputProvider().nextLine().trim();
            File file = new File(fileName);
            if (!file.exists()) {
                throw new Exception("File " + fileName + " does not exist.");
            }
            userInputProviderQueue.add(new Scanner(new FileInputStream(file)).useDelimiter(delimiterRegex));
        }));
    }
}

package ru.ifmo.se.s267880.computationalMath.lab1;

import java.util.*;

public class App {
    public static final String delimiterRegex = "[\\s;,]+";
    public static void main(String[] args) {
        Scanner argInputProvider = new Scanner(String.join(" ", args)).useDelimiter(delimiterRegex);
        new App(argInputProvider);
    }

    boolean usingInteractive = false;
    Map<String, Command> commands = new LinkedHashMap<>();
    Deque<Scanner> userInputProviderQueue = new LinkedList<>();

    public App(Scanner ...inputProviders) {
        userInputProviderQueue.addAll(Arrays.asList(inputProviders));
        initCommands();
        loop();
    }

    void loop() {
        while (!userInputProviderQueue.isEmpty()) {
            if (!getCurrentInputProvider().hasNext()) {
                userInputProviderQueue.removeLast();
                continue;
            }
            String commandName = getCurrentInputProvider().next();
            if (commandName.charAt(0) != '-') {
                System.err.printf("Expected commandName (begin with '-'), but got \"%s\"\n", commandName);
                continue;
            }

            commandName = commandName.substring(1);
            if (!commands.containsKey(commandName)) {
                System.err.printf("There is no command with name \"%s\"\n", commandName);
                continue;
            }
            try {
                commands.get(commandName).process();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    Scanner getCurrentInputProvider() {
        return userInputProviderQueue.getLast();
    }

    void initCommands() {
        commands.put("help", new Command("Print this message", () -> {
            commands.forEach((commandName, command) -> {
                System.out.printf("\t-%-15s - %s\n", commandName, command.getUsage());
            });
        }));
        commands.put("hello-world", new Command("Print hello world, just for testing :))", () -> System.out.println("Hello world")));
        commands.put("i", new Command("Enter interactive mode.", () -> {
            if (usingInteractive) {
                System.err.println("You are already in the interactive mode.");
                return ;
            }
            userInputProviderQueue.addFirst(new Scanner(System.in).useDelimiter(delimiterRegex));
            usingInteractive = true;
        }));
        commands.put("exit", new Command("Wonder what does this command do???", () -> System.exit(0)));
    }
}

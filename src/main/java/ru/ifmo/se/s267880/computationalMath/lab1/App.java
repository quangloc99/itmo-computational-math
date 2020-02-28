package ru.ifmo.se.s267880.computationalMath.lab1;

import java.util.*;

public class App {
    public static final String delimiterRegex = "[\\s;,]+";
    public static void main(String[] args) {
        Scanner argInputProvider = new Scanner(String.join(" ", args)).useDelimiter(delimiterRegex);
        new App(argInputProvider);
    }

    Map<String, Command> commands = new HashMap<>();
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
        commands.put("hello-world", new Command("Print hello world", () -> {
            System.out.println("Hello world");
        }));
        commands.put("i", new Command("Enter interactive mode", () -> {
            userInputProviderQueue.addFirst(new Scanner(System.in).useDelimiter(delimiterRegex));
        }));
    }
}

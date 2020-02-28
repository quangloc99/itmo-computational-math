package ru.ifmo.se.s267880.computationalMath.lab1;

public class Command {
    @FunctionalInterface
    interface Handler {
        void process() throws Exception;
    }

    private String usage;
    private String argumentDescription = "";
    private Handler handler;

    public Command(String usage, Handler handler) {
        this.usage = usage;
        this.handler = handler;
    }

    public Command(String usage, String argumentDescription, Handler handler) {
        this.usage = usage;
        this.argumentDescription = argumentDescription;
        this.handler = handler;
    }

    public void process() throws Exception {
        this.handler.process();
    }

    public String getUsage() {
        return usage;
    }

    public String getArgumentDescription() {
        return argumentDescription;
    }
}

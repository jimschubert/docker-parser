package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents a SHELL instruction.
 */
public class ShellInstruction extends DockerInstruction {
    private final List<String> commands;

    /**
     * Creates a new instance of ShellInstruction.
     *
     * @param commands The commands to run.
     */
    public ShellInstruction(List<String> commands) {
        super("SHELL");
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction() + " [");
        for (int i = 0; i < commands.size(); i++) {
            sb.append("\"").append(commands.get(i)).append("\"");
            if (i < commands.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}

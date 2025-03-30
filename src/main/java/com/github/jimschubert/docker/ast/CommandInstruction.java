package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents a command instruction in a Dockerfile.
 */
public abstract class CommandInstruction extends DockerInstruction {
    public enum Form {
        SHELL,
        EXEC
    }

    private Form form;
    private List<String> command;

    /**
     * Creates a new instance of CommandInstruction.
     *
     * @param instruction The instruction to set.
     */
    public CommandInstruction(String instruction) {
        super(instruction);
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public List<String> getCommand() {
        return command;
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }

    @Override
    public String toCanonicalForm() {
        if (form == Form.EXEC) {
            StringBuilder sb = new StringBuilder(getInstruction() + " [");
            for (int i = 0; i < command.size(); i++) {
                sb.append("\"").append(command.get(i)).append("\"");
                if (i < command.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            return sb.toString();
        } else {
            return getInstruction() + " " + String.join(" ", command);
        }
    }
}
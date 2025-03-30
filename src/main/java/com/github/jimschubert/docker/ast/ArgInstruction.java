package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents an ARG instruction in a Dockerfile.
 */
public class ArgInstruction extends DockerInstruction {
    private List<KeyValuePair> args;

    /**
     * Creates a new instance of ArgInstruction.
     *
     * @param args The arguments to set.
     */
    public ArgInstruction(List<KeyValuePair> args) {
        super("ARG");
        this.args = args;
    }

    public List<KeyValuePair> getArgs() {
        return args;
    }

    public void setArgs(List<KeyValuePair> args) {
        this.args = args;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction());
        for (KeyValuePair arg : args) {
            sb.append(" ").append(arg.getKey());
            if (arg.hasEquals()) {
                sb.append("=");
            }
            switch (arg.getQuoting()) {
                case SINGLE_QUOTED -> sb.append("'").append(arg.getValue()).append("'");
                case DOUBLE_QUOTED -> sb.append("\"").append(arg.getValue()).append("\"");
                default -> sb.append(arg.getValue());
            };
        }
        return sb.toString();
    }
}
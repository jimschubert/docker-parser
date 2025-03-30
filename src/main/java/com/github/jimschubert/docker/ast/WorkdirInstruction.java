package com.github.jimschubert.docker.ast;

/**
 * Represents a WORKDIR instruction.
 */
public class WorkdirInstruction extends DockerInstruction {
    private final String workdir;

    /**
     * Creates a new instance of WorkdirInstruction.
     *
     * @param workdir The working directory to set.
     */
    public WorkdirInstruction(String workdir) {
        super("WORKDIR");
        this.workdir = workdir;
    }

    public String getWorkdir() {
        return workdir;
    }

    @Override
    public String toCanonicalForm() {
        return String.format("%s %s", getInstruction(), workdir);
    }
}

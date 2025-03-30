package com.github.jimschubert.docker.ast;

/**
 * Represents a Docker instruction.
 */
public abstract class DockerInstruction {
    String instruction;

    public String getInstruction() {
        return instruction;
    }

    /**
     * Creates a new instance of DockerInstruction.
     *
     * @param instruction The instruction to set.
     */
    public DockerInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * Converts the instruction to its canonical form.
     *
     * @return The instruction in canonical form.
     */
    public abstract String toCanonicalForm();
}

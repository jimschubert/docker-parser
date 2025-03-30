package com.github.jimschubert.docker.ast;

/**
 * Represents a MAINTAINER instruction.
 */
public class MaintainerInstruction extends DockerInstruction {
    private final String maintainer;

    /**
     * Creates a new instance of MaintainerInstruction.
     *
     * @param maintainer The maintainer to set.
     */
    public MaintainerInstruction(String maintainer) {
        super("MAINTAINER");
        this.maintainer = maintainer;
    }

    public String getMaintainer() {
        return maintainer;
    }

    @Override
    public String toCanonicalForm() {
        return String.format("%s %s", getInstruction(), maintainer);
    }
}

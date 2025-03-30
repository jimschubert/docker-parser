package com.github.jimschubert.docker.ast;

/**
 * Represents an ONBUILD instruction.
 */
public class OnBuildInstruction extends DockerInstruction {
    private DockerInstruction deferredInstruction;

    public DockerInstruction getDeferredInstruction() {
        return deferredInstruction;
    }

    public void setDeferredInstruction(DockerInstruction deferredInstruction) {
        this.deferredInstruction = deferredInstruction;
    }

    /**
     * Creates a new instance of OnBuildInstruction.
     *
     * @param instruction The instruction to defer.
     */
    public OnBuildInstruction(DockerInstruction instruction) {
        super("ONBUILD");
        deferredInstruction = instruction;
    }

    @Override
    public String toCanonicalForm() {
        return String.format("%s %s", getInstruction(), deferredInstruction.toCanonicalForm());
    }
}

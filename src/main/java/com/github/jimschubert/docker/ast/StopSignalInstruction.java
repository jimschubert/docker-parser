package com.github.jimschubert.docker.ast;

/**
 * Represents a STOPSIGNAL instruction.
 */
public class StopSignalInstruction extends DockerInstruction {
    private final String signal;

    /**
     * Creates a new instance of StopSignalInstruction.
     *
     * @param signal The signal to stop.
     */
    public StopSignalInstruction(String signal) {
        super("STOPSIGNAL");
        this.signal = signal;
    }

    public String getSignal() {
        return signal;
    }

    @Override
    public String toCanonicalForm() {
        return getInstruction() + " " + signal;
    }
}
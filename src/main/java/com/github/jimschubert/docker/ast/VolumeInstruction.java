package com.github.jimschubert.docker.ast;

/**
 * Represents a VOLUME instruction.
 */
public class VolumeInstruction extends CommandInstruction {
    /**
     * Creates a new instance of VolumeInstruction.
     */
    public VolumeInstruction() {
        super("VOLUME");
    }

    public String getVolume() {
        if (getCommand().isEmpty()) {
            return null;
        }
        return getCommand().get(0);
    }
}

package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents an ENTRYPOINT instruction.
 */
public class EntrypointInstruction extends CommandInstruction {
    /**
     * Creates a new instance of EntrypointInstruction.
     */
    public EntrypointInstruction() {
        super("ENTRYPOINT");
    }

    public List<String> getEntrypoint() {
        return getCommand();
    }
}

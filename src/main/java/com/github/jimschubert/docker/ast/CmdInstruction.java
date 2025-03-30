package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents a CMD instruction.
 */
public class CmdInstruction extends CommandInstruction {
    /**
     * Creates a new instance of CmdInstruction.
     */
    public CmdInstruction() {
        super("CMD");
    }

    public CmdInstruction(Form form, List<String> commands) {
        super("CMD");
        setForm(form);
        setCommand(commands);
    }
}
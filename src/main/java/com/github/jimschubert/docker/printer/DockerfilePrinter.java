package com.github.jimschubert.docker.printer;

import com.github.jimschubert.docker.ast.DockerInstruction;

import java.util.List;

public class DockerfilePrinter {
    private final List<DockerInstruction> instructions;

    public DockerfilePrinter(List<DockerInstruction> instructions) {
        this.instructions = instructions;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        for (DockerInstruction instruction : instructions) {
            sb.append(instruction.toCanonicalForm()).append("\n");
        }
        return sb.toString();
    }
}

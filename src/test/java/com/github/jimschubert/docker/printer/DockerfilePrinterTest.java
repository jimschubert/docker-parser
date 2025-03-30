package com.github.jimschubert.docker.printer;

import com.github.jimschubert.docker.ast.CopyInstruction;
import com.github.jimschubert.docker.ast.DockerInstruction;
import com.github.jimschubert.docker.ast.FromInstruction;
import com.github.jimschubert.docker.ast.RunInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DockerfilePrinterTest {

    @Test
    void testPrint() {
        DockerInstruction instruction1 = new FromInstruction("", "openjdk:17", "", "");
        DockerInstruction instruction2 = new CopyInstruction(List.of("."), "/app");
        DockerInstruction instruction3 = new RunInstruction(List.of("javac Main.java"));

        List<DockerInstruction> instructions = List.of(instruction1, instruction2, instruction3);
        DockerfilePrinter printer = new DockerfilePrinter(instructions);

        String expectedOutput = "FROM openjdk:17\nCOPY . /app\nRUN javac Main.java\n";
        String actualOutput = printer.print();

        assertEquals(expectedOutput, actualOutput);
    }
}
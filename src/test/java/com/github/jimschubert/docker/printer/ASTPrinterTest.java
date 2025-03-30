package com.github.jimschubert.docker.printer;

import com.github.jimschubert.docker.ast.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ASTPrinterTest {

    @Test
    void testToASTStringWithKeyValuePair() {
        KeyValuePair pair = new KeyValuePair("key", "value", true, Quoting.DOUBLE_QUOTED);
        String expected = "KeyValuePair {\n  key: key, value: value, hasEquals: true, quoting: DOUBLE_QUOTED\n}";
        String actual = ASTPrinter.toASTString(pair);
        assertEquals(expected, actual);
    }

    @Test
    void testToASTStringWithEnvVariable() {
        EnvVariable env = new EnvVariable("key", "value", true, Quoting.SINGLE_QUOTED);
        String expected = "EnvVariable {\n  key: key, value: value, deprecatedSyntax: true, quoting: SINGLE_QUOTED\n}";
        String actual = ASTPrinter.toASTString(env);
        assertEquals(expected, actual);
    }

    @Test
    void testToASTStringWithCommandInstruction() {
        CmdInstruction cmd = new CmdInstruction(CmdInstruction.Form.SHELL, List.of("echo", "Hello World"));
        String expected = "CmdInstruction {\n  form: SHELL, command: [echo, Hello World]\n}";
        String actual = ASTPrinter.toASTString(cmd);
        assertEquals(expected, actual);
    }
}
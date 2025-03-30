package com.github.jimschubert.docker.parser;

import org.junit.jupiter.api.Test;
import com.github.jimschubert.docker.ast.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DockerfileParserTest {

    @Test
    void testParseDockerfileWithComments() throws Exception {
        String dockerfileContent = """
            # This is a comment
            FROM openjdk:17
            # Another comment
            ENV APP_HOME=/app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(4, instructions.size());
        assertInstanceOf(CommentInstruction.class, instructions.get(0));
        assertInstanceOf(FromInstruction.class, instructions.get(1));
        assertInstanceOf(CommentInstruction.class, instructions.get(2));
    }

    @Test
    void testParseDockerfileWithMultilineComments() throws Exception {
        String dockerfileContent = """
            # This is a comment
            # that spans multiple lines
            FROM openjdk:17
            # Another comment
            ENV APP_HOME=/app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(4, instructions.size());
        assertInstanceOf(CommentInstruction.class, instructions.get(0));
        assertEquals("This is a comment\nthat spans multiple lines", ((CommentInstruction)instructions.get(0)).getComment());
        assertInstanceOf(FromInstruction.class, instructions.get(1));
        assertInstanceOf(CommentInstruction.class, instructions.get(2));
    }


    @Test
    void testParseDockerfileWithMultilineCommentsFollowingSingleLine() throws Exception {
        String dockerfileContent = """
            # This is a single comment
            
            # This is a comment
            # that spans multiple lines
            FROM openjdk:17
            # Another comment
            ENV APP_HOME=/app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(5, instructions.size());
        assertInstanceOf(CommentInstruction.class, instructions.get(0));
        assertEquals("This is a single comment", ((CommentInstruction)instructions.get(0)).getComment());
        assertInstanceOf(CommentInstruction.class, instructions.get(1));
        assertEquals("This is a comment\nthat spans multiple lines", ((CommentInstruction)instructions.get(1)).getComment());
        assertInstanceOf(FromInstruction.class, instructions.get(2));
        assertInstanceOf(CommentInstruction.class, instructions.get(3));
    }


    @Test
    void testParseDockerfileWithEscapeDirective() throws Exception {
        String dockerfileContent = """
            # escape=`
            FROM openjdk:17
            RUN mkdir -p `
                /app/logs
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(3, instructions.size());
        assertInstanceOf(DirectiveInstruction.class, instructions.get(0));
        assertInstanceOf(RunInstruction.class, instructions.get(2));
        assertEquals("mkdir -p /app/logs", ((RunInstruction)instructions.get(2)).getCommands().get(0));
    }

    @Test
    void testParseDockerfileWithCopyInstruction() throws Exception {
        String dockerfileContent = """
            FROM openjdk:17
            COPY --chown=1001:1001 --from=builder /src /app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(2, instructions.size());
        assertInstanceOf(CopyInstruction.class, instructions.get(1));
        CopyInstruction copyInstruction = (CopyInstruction) instructions.get(1);
        assertEquals("1001:1001", copyInstruction.getChown());
        assertEquals("builder", copyInstruction.getFrom());
        assertEquals("/app", copyInstruction.getDestination());
        assertEquals(List.of("/src"), copyInstruction.getSources());
    }

    @Test
    void testParseDockerfileWithRunInstruction() throws Exception {
        String dockerfileContent = """
            FROM openjdk:17
            RUN mkdir -p /app/logs && chown -R 1001:1001 /app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(2, instructions.size());
        assertInstanceOf(RunInstruction.class, instructions.get(1));
        RunInstruction runInstruction = (RunInstruction) instructions.get(1);
        assertEquals("mkdir -p /app/logs && chown -R 1001:1001 /app", runInstruction.getCommands().get(0));
    }

    @Test
    void testParseDockerfileWithEnvInstruction() throws Exception {
        String dockerfileContent = """
            FROM openjdk:17
            ENV APP_HOME=/app
            """;
        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parseDockerfile(inputStream);

        assertEquals(2, instructions.size());
        assertInstanceOf(EnvInstruction.class, instructions.get(1));
        EnvInstruction envInstruction = (EnvInstruction) instructions.get(1);
        assertEquals(1, envInstruction.getVariables().size());
        assertEquals("APP_HOME", envInstruction.getVariables().get(0).getKey());
        assertEquals("/app", envInstruction.getVariables().get(0).getValue());
    }
}
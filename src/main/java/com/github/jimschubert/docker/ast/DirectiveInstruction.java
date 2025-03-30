package com.github.jimschubert.docker.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a directive instruction in a Dockerfile.
 */
public class DirectiveInstruction extends DockerInstruction {
    private final Map<String, String> directives = new HashMap<>();

    /**
     * Creates a new instance of DirectiveInstruction.
     *
     * @param directive The directive to set.
     */
    public DirectiveInstruction(String directive) {
        super("#");
        parseDirectives(directive);
    }

    private void parseDirectives(String directive) {
        String[] parts = directive.split("\\s+");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                directives.put(keyValue[0], keyValue[1]);
            } else {
                directives.put(keyValue[0], "");
            }
        }
    }

    public Map<String, String> getDirectives() {
        return directives;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder("#");
        directives.forEach((key, value) -> sb.append(" ").append(key).append("=").append(value));
        return sb.toString();
    }
}
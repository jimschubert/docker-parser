package com.github.jimschubert.docker.ast;

/**
 * Represents an environment variable.
 */
public class EnvVariable extends KeyValuePair {
    /**
     * Creates a new instance of EnvVariable.
     *
     * @param key The key to set.
     * @param value The value to set.
     * @param deprecatedSyntax Whether the syntax is deprecated.
     * @param quoting The quoting to use.
     */
    public EnvVariable(String key, String value, boolean deprecatedSyntax, Quoting quoting) {
        super(key, value, !deprecatedSyntax, quoting);
    }

    public boolean isDeprecatedSyntax() {
        return !super.hasEquals();
    }
}
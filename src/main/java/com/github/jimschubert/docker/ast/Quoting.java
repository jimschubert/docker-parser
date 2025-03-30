package com.github.jimschubert.docker.ast;

/**
 * Represents a quoting style.
 */
public enum Quoting {
    /**
     * Represents an unquoted value.
     */
    UNQUOTED,
    /**
     * Represents a single-quoted value.
     */
    SINGLE_QUOTED,
    /**
     * Represents a double-quoted value.
     */
    DOUBLE_QUOTED
}

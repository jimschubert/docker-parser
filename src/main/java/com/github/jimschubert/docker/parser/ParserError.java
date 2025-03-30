package com.github.jimschubert.docker.parser;

/**
 * Represents an error that occurred during parsing.
 */
public class ParserError extends Exception {
    /**
     * Creates a new instance of ParserError.
     * @param message The error message.
     */
    public ParserError(String message) {
        super(message);
    }

    /**
     * Creates a new instance of ParserError.
     * @param message The error message.
     * @param cause The cause of the error.
     */
    public ParserError(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.github.jimschubert.docker.ast;

/**
 * Represents a COMMENT instruction.
 */
public class CommentInstruction extends DockerInstruction {
    private String comment;

    /**
     * Creates a new instance of CommentInstruction.
     *
     * @param comment The comment to set.
     */
    public CommentInstruction(String comment) {
        super("COMMENT");
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toCanonicalForm() {
        return "# " + comment.replace("\n", "\n# ");
    }
}
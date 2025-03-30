package com.github.jimschubert.docker.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a LABEL instruction in a Dockerfile.
 */
public class LabelInstruction extends DockerInstruction {
    private List<KeyValuePair> labels = new ArrayList<>();

    /**
     * Creates a new instance of LabelInstruction.
     *
     * @param labels The labels to set.
     */
    public LabelInstruction(List<KeyValuePair> labels) {
        super("LABEL");
        this.labels.addAll(labels);
    }

    public List<KeyValuePair> getLabels() {
        return labels;
    }

    public void setLabels(List<KeyValuePair> labels) {
        this.labels = labels;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder("LABEL");
        for (KeyValuePair label : labels) {
            sb.append(" ").append(label.getKey());
            if (label.hasEquals()) {
                sb.append("=");
            }
            if (label.getQuoting() == Quoting.SINGLE_QUOTED) {
                sb.append("'").append(label.getValue()).append("'");
            } else if (label.getQuoting() == Quoting.DOUBLE_QUOTED) {
                sb.append("\"").append(label.getValue()).append("\"");
            } else {
                sb.append(label.getValue());
            }
        }
        return sb.toString();
    }
}
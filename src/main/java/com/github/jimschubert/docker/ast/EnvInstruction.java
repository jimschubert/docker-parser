package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents an ENV instruction in a Dockerfile.
 */
public class EnvInstruction extends DockerInstruction {
    private List<EnvVariable> variables;

    /**
     * Creates a new instance of EnvInstruction.
     *
     * @param variables The variables to set.
     */
    public EnvInstruction(List<EnvVariable> variables) {
        super("ENV");
        this.variables = variables;
    }

    public List<EnvVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<EnvVariable> variables) {
        this.variables = variables;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder("ENV");
        for (EnvVariable variable : variables) {
            sb.append(" ").append(variable.getKey());
            if (variable.isDeprecatedSyntax()) {
                sb.append(" ");
            } else {
                sb.append("=");
            }
            sb.append(formatValue(variable));
        }
        return sb.toString();
    }

    /**
     * Formats the value of an environment variable.
     *
     * @param variable The variable to format.
     * @return The formatted value.
     */
    private String formatValue(EnvVariable variable) {
        return switch (variable.getQuoting()) {
            case SINGLE_QUOTED -> "'" + variable.getValue() + "'";
            case DOUBLE_QUOTED -> "\"" + variable.getValue() + "\"";
            default -> variable.getValue();
        };
    }
}
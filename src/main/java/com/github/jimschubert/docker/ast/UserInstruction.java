package com.github.jimschubert.docker.ast;

/**
 * Represents a USER instruction.
 */
public class UserInstruction extends DockerInstruction {
    private String user;
    private String group;

    /**
     * Creates a new instance of UserInstruction.
     *
     * @param user The user to set.
     */
    public UserInstruction(String user) {
        this(user, null);
    }

    /**
     * Creates a new instance of UserInstruction.
     *
     * @param user The user to set.
     * @param group The group to set.
     */
    public UserInstruction(String user, String group) {
        super("USER");
        this.user = user;
        this.group = group;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toCanonicalForm() {
        return String.format("%s %s%s%s", getInstruction(), user, group != null ? ":" : "", group != null ? group : "");
    }
}

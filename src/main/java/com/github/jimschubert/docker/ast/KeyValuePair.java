package com.github.jimschubert.docker.ast;

/**
 * Represents a key-value pair.
 */
public class KeyValuePair {
    private String key;
    private String value;
    private boolean hasEquals;
    private Quoting quoting;

    /**
     * Creates a new instance of KeyValuePair.
     *
     * @param key The key to set.
     * @param value The value to set.
     * @param hasEquals Whether the key-value pair has an equals sign.
     * @param quoting The quoting to use.
     */
    public KeyValuePair(String key, String value, boolean hasEquals, Quoting quoting) {
        this.key = key;
        this.value = value;
        this.hasEquals = hasEquals;
        this.quoting = quoting;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean hasEquals() {
        return hasEquals;
    }

    public void setHasEquals(boolean hasEquals) {
        this.hasEquals = hasEquals;
    }

    public Quoting getQuoting() {
        return quoting;
    }

    public void setQuoting(Quoting quoting) {
        this.quoting = quoting;
    }

    /**
     * Converts the key-value pair to its canonical form.
     *
     * @return The key-value pair in canonical form.
     */
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getKey());
        if (hasEquals()) {
            sb.append("=");
        }
        if (getQuoting() == Quoting.SINGLE_QUOTED) {
            sb.append("'").append(getValue()).append("'");
        } else if (getQuoting() == Quoting.DOUBLE_QUOTED) {
            sb.append("\"").append(getValue()).append("\"");
        } else {
            sb.append(getValue());
        }
        return sb.toString();
    }
}

package com.github.jimschubert.docker.ast;

/**
 * Represents a HEALTHCHECK instruction.
 */
public class HealthCheckInstruction extends DockerInstruction {
    /**
     * Represents the type of health check.
     */
    public enum HealthCheckType {
        CMD, NONE
    }

    private final HealthCheckType type;
    private final String test;
    private final String interval;
    private final String timeout;
    private final String startPeriod;
    private final String retries;

    /**
     * Creates a new instance of HealthCheckInstruction.
     *
     * @param type The type of health check.
     * @param test The test to run.
     * @param interval The interval to run the test.
     * @param timeout The timeout for the test.
     * @param startPeriod The start period for the test.
     * @param retries The number of retries.
     */
    public HealthCheckInstruction(HealthCheckType type, String test, String interval, String timeout, String startPeriod, String retries) {
        super("HEALTHCHECK");
        this.type = type;
        this.test = test;
        this.interval = interval;
        this.timeout = timeout;
        this.startPeriod = startPeriod;
        this.retries = retries;
    }

    /**
     * Creates a new instance of HealthCheckInstruction.
     */
    public HealthCheckInstruction() {
        super("HEALTHCHECK");
        this.type = HealthCheckType.NONE;
        this.test = null;
        this.interval = null;
        this.timeout = null;
        this.startPeriod = null;
        this.retries = null;
    }

    public HealthCheckType getType() {
        return type;
    }

    public String getTest() {
        return test;
    }

    public String getInterval() {
        return interval;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public String getRetries() {
        return retries;
    }

    @Override
    public String toCanonicalForm() {
        if (type == HealthCheckType.NONE) {
            return getInstruction() + " NONE";
        }

        StringBuilder sb = new StringBuilder(getInstruction());
        sb.append(" CMD ");
        sb.append(test);
        if (interval != null) {
            sb.append(" --interval=").append(interval);
        }
        if (timeout != null) {
            sb.append(" --timeout=").append(timeout);
        }
        if (startPeriod != null) {
            sb.append(" --start-period=").append(startPeriod);
        }
        if (retries != null) {
            sb.append(" --retries=").append(retries);
        }
        return sb.toString();
    }
}
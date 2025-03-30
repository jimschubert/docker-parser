package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents an EXPOSE instruction.
 */
public class ExposeInstruction extends DockerInstruction {
    /**
     * Represents a port to expose.
     */
    public static class Port {
        private String port;
        private String protocol;
        private boolean protocolProvided;

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public boolean isProtocolProvided() {
            return protocolProvided;
        }

        public void setProtocolProvided(boolean protocolProvided) {
            this.protocolProvided = protocolProvided;
        }

        /**
         * Creates a new instance of Port.
         *
         * @param port The port to expose.
         */
        public Port(String port) {
            this.port = port;
            this.protocol = "tcp";
            this.protocolProvided = false;
        }

        /**
         * Creates a new instance of Port.
         *
         * @param port The port to expose.
         * @param protocol The protocol to expose.
         */
        public Port(String port, String protocol) {
            this.port = port;
            this.protocol = protocol;
            this.protocolProvided = true;
        }
    }

    private List<Port> ports;

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }


    /**
     * Creates a new instance of ExposeInstruction.
     *
     * @param ports The ports to expose.
     */
    public ExposeInstruction(List<Port> ports) {
        super("EXPOSE");
        this.ports = ports;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction());
        for (Port port : ports) {
            sb.append(" ").append(port.getPort());
            if (port.isProtocolProvided() && !"".equals(port.getProtocol())) {
                sb.append("/").append(port.getProtocol());
            }
        }
        return sb.toString();
    }
}

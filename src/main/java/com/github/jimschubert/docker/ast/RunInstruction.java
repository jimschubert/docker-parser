package com.github.jimschubert.docker.ast;

import java.util.Collections;
import java.util.List;

public class RunInstruction extends DockerInstruction {
    private final List<String> commands;
    private final List<Mount> mounts;
    private final NetworkOption networkOption;
    private final SecurityOption securityOption;
    private final String heredoc;
    private final String heredocName;

    public RunInstruction(List<String> commands, List<Mount> mounts, NetworkOption networkOption, SecurityOption securityOption, String heredoc, String heredocName) {
        super("RUN");
        this.commands = commands;
        this.mounts = mounts;
        this.networkOption = networkOption;
        this.securityOption = securityOption;
        this.heredoc = heredoc;
        this.heredocName = heredocName;
    }

    public RunInstruction(List<String> commands) {
        this(commands, Collections.emptyList(), null, null, null, null);
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<Mount> getMounts() {
        return mounts;
    }

    public NetworkOption getNetworkOption() {
        return networkOption;
    }

    public SecurityOption getSecurityOption() {
        return securityOption;
    }

    public String getHeredoc() {
        return heredoc;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction());

        for (Mount mount : mounts) {
            if (mount.getType() == null) {
                sb.append(" --mount=target=").append(mount.getTarget());
            } else {
                sb.append(" --mount=type=").append(mount.getType())
                        .append(",target=").append(mount.getTarget());
                if (mount.getId() != null) {
                    sb.append(",id=").append(mount.getId());
                }
            }
        }

        if (networkOption != null) {
            sb.append(" --network=").append(networkOption.name().toLowerCase());
        }

        if (securityOption != null) {
            sb.append(" --security=").append(securityOption.name().toLowerCase());
        }

        for (String command : commands) {
            sb.append(" ").append(command);
        }

        if (heredoc != null) {
            sb.append("<<").append(heredocName).append("\n").append(heredoc).append("\n").append(heredocName);
        }

        return sb.toString();
    }

    public static class Mount {
        private final String type;
        private final String target;
        private final String id;

        public Mount(String type, String target, String id) {
            this.type = type;
            this.target = target;
            this.id = id;
        }

        public Mount(String target) {
            this.type = null;
            this.target = target;
            this.id = null;
        }

        public String getType() {
            return type;
        }

        public String getTarget() {
            return target;
        }

        public String getId() {
            return id;
        }
    }

    public enum NetworkOption {
        DEFAULT, NONE, HOST
    }

    public enum SecurityOption {
        DEFAULT, INSECURE
    }
}
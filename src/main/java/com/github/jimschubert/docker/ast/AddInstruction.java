package com.github.jimschubert.docker.ast;

import java.util.List;

public class AddInstruction extends DockerInstruction {
    private final List<String> sources;
    private final String destination;
    private final Boolean keepGitDir;
    private final String checksum;
    private final String chown;
    private final String chmod;
    private final Boolean link;
    private final List<String> exclude;

    public AddInstruction(List<String> sources, String destination, Boolean keepGitDir, String checksum, String chown, String chmod, Boolean link, List<String> exclude) {
        super("ADD");
        this.sources = sources;
        this.destination = destination;
        this.keepGitDir = keepGitDir;
        this.checksum = checksum;
        this.chown = chown;
        this.chmod = chmod;
        this.link = link;
        this.exclude = exclude;
    }

    public List<String> getSources() {
        return sources;
    }

    public String getDestination() {
        return destination;
    }

    public Boolean getKeepGitDir() {
        return keepGitDir;
    }

    public String getChecksum() {
        return checksum;
    }

    public String getChown() {
        return chown;
    }

    public String getChmod() {
        return chmod;
    }

    public Boolean getLink() {
        return link;
    }

    public List<String> getExclude() {
        return exclude;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction());

        if (keepGitDir != null) {
            sb.append(" --keep-git-dir=").append(keepGitDir);
        }
        if (checksum != null) {
            sb.append(" --checksum=").append(checksum);
        }
        if (chown != null) {
            sb.append(" --chown=").append(chown);
        }
        if (chmod != null) {
            sb.append(" --chmod=").append(chmod);
        }
        if (link != null) {
            sb.append(" --link=").append(link);
        }
        if (exclude != null && !exclude.isEmpty()) {
            for (String path : exclude) {
                sb.append(" --exclude=").append(path);
            }
        }

        for (String source : sources) {
            sb.append(" ").append(source);
        }
        sb.append(" ").append(destination);

        return sb.toString();
    }
}
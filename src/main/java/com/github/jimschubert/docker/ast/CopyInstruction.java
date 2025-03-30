package com.github.jimschubert.docker.ast;

import java.util.List;

/**
 * Represents a COPY instruction.
 */
public class CopyInstruction extends DockerInstruction {
    private List<String> sources;
    private String destination;
    private String from;
    private String chown;
    private String chmod;
    private Boolean link;
    private Boolean parents;
    private List<String> exclude;

    /**
     * Creates a new instance of CopyInstruction.
     *
     * @param sources The sources to copy.
     * @param destination The destination to copy to.
     * @param from The source image, stage, or context.
     * @param chown The user or user:group to change ownership to.
     * @param chmod The permissions to set.
     * @param link Whether to create a hard link.
     * @param parents Whether to create parent directories.
     * @param exclude The paths to exclude.
     */
    public CopyInstruction(List<String> sources, String destination, String from, String chown, String chmod, Boolean link, Boolean parents, List<String> exclude) {
        super("COPY");
        this.sources = sources;
        this.destination = destination;
        this.from = from;
        this.chown = chown;
        this.chmod = chmod;
        this.link = link;
        this.parents = parents;
        this.exclude = exclude;
    }

    public CopyInstruction(List<String> sources, String destination) {
        this(sources, destination, null, null, null, null, null, null);
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getChown() {
        return chown;
    }

    public void setChown(String chown) {
        this.chown = chown;
    }

    public String getChmod() {
        return chmod;
    }

    public void setChmod(String chmod) {
        this.chmod = chmod;
    }

    public Boolean getLink() {
        return link;
    }

    public void setLink(Boolean link) {
        this.link = link;
    }

    public Boolean getParents() {
        return parents;
    }

    public void setParents(Boolean parents) {
        this.parents = parents;
    }

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder(getInstruction());

        if (from != null) {
            sb.append(" --from=").append(from);
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
        if (parents != null) {
            sb.append(" --parents=").append(parents);
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
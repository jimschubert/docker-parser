package com.github.jimschubert.docker.ast;

/**
 * Represents a FROM instruction.
 */
public class FromInstruction extends DockerInstruction {
    private String platform;
    private String image;
    private String digest;
    private String alias;

    /**
     * Creates a new instance of FromInstruction.
     *
     * @param platform The platform to use.
     * @param image The image to use.
     * @param digest The digest of the image.
     * @param alias The alias to use.
     */
    public FromInstruction(String platform, String image, String digest, String alias) {
        super("FROM");
        this.platform = platform;
        this.image = image;
        this.digest = digest;
        this.alias = alias;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toCanonicalForm() {
        StringBuilder sb = new StringBuilder("FROM ");
        if (platform != null && !platform.isEmpty()) {
            sb.append("--platform=").append(platform).append(" ");
        }
        sb.append(image);
        if (digest != null && !digest.isEmpty()) {
            sb.append("@").append(digest);
        }
        if (alias != null && !alias.isEmpty()) {
            sb.append(" AS ").append(alias);
        }
        return sb.toString();
    }
}
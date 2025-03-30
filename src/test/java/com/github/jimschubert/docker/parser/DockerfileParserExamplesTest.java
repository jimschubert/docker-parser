package com.github.jimschubert.docker.parser;

import org.junit.jupiter.api.Test;
import com.github.jimschubert.docker.ast.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DockerfileParserExamplesTest {
    private List<DockerInstruction> parseFromResource(final String resourcePath) throws ParserError, IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        assertNotNull(inputStream, "Dockerfile resource not found: " + resourcePath);

        DockerfileParser parser = new DockerfileParser();
        return parser.parseDockerfile(inputStream);
    }

    @Test
    public void testHugoDockerfileFull() throws ParserError, IOException {
        List<DockerInstruction> instructions = parseFromResource("examples/hugo/Dockerfile");

        assertFalse(instructions.isEmpty(), "Parsed instructions should not be empty");

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(0));
        assertEquals(
                "GitHub:       https://github.com/gohugoio\n" +
                "Twitter:      https://twitter.com/gohugoio\n" +
                "Website:      https://gohugo.io/",
                ((CommentInstruction) instructions.get(0)).getComment());

        // ARG
        assertInstanceOf(ArgInstruction.class, instructions.get(1));
        assertEquals("GO_VERSION=\"1.23.2\"", ((ArgInstruction) instructions.get(1)).getArgs().get(0).toCanonicalForm());
        assertInstanceOf(ArgInstruction.class, instructions.get(2));
        assertEquals("ALPINE_VERSION=\"3.20\"", ((ArgInstruction) instructions.get(2)).getArgs().get(0).toCanonicalForm());
        assertInstanceOf(ArgInstruction.class, instructions.get(3));
        assertEquals("DART_SASS_VERSION=\"1.79.3\"", ((ArgInstruction) instructions.get(3)).getArgs().get(0).toCanonicalForm());

        // FROM
        assertInstanceOf(FromInstruction.class, instructions.get(4));
        assertEquals("FROM --platform=$BUILDPLATFORM tonistiigi/xx:1.5.0 AS xx", instructions.get(4).toCanonicalForm());
        assertEquals("$BUILDPLATFORM", ((FromInstruction) instructions.get(4)).getPlatform());
        assertEquals("tonistiigi/xx:1.5.0", ((FromInstruction) instructions.get(4)).getImage());
        assertEquals("xx", ((FromInstruction) instructions.get(4)).getAlias());

        assertInstanceOf(FromInstruction.class, instructions.get(5));
        assertEquals("FROM --platform=$BUILDPLATFORM golang:${GO_VERSION}-alpine${ALPINE_VERSION} AS gobuild", instructions.get(5).toCanonicalForm());
        assertEquals("$BUILDPLATFORM", ((FromInstruction) instructions.get(5)).getPlatform());
        assertEquals("golang:${GO_VERSION}-alpine${ALPINE_VERSION}", ((FromInstruction) instructions.get(5)).getImage());
        assertEquals("gobuild", ((FromInstruction) instructions.get(5)).getAlias());
        assertInstanceOf(FromInstruction.class, instructions.get(6));

        assertEquals("FROM golang:${GO_VERSION}-alpine${ALPINE_VERSION} AS gorun", instructions.get(6).toCanonicalForm());
        assertNull(((FromInstruction) instructions.get(6)).getPlatform());
        assertEquals("golang:${GO_VERSION}-alpine${ALPINE_VERSION}", ((FromInstruction) instructions.get(6)).getImage());
        assertEquals("gorun", ((FromInstruction) instructions.get(6)).getAlias());

        assertEquals("FROM gobuild AS build", instructions.get(7).toCanonicalForm());
        assertNull(((FromInstruction) instructions.get(7)).getPlatform());
        assertEquals("gobuild", ((FromInstruction) instructions.get(7)).getImage());
        assertEquals("build", ((FromInstruction) instructions.get(7)).getAlias());

        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(8));
        assertEquals("RUN apk add clang lld", instructions.get(8).toCanonicalForm());
        assertEquals("apk add clang lld", ((RunInstruction) instructions.get(8)).getCommands().get(0));

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(9));
        assertEquals("Set up cross-compilation helpers", ((CommentInstruction) instructions.get(9)).getComment());

        // COPY
        assertInstanceOf(CopyInstruction.class, instructions.get(10));
        assertEquals("COPY --from=xx / /", instructions.get(10).toCanonicalForm());
        assertEquals("xx", ((CopyInstruction) instructions.get(10)).getFrom());

        // ARG
        assertInstanceOf(ArgInstruction.class, instructions.get(11));
        assertEquals("ARG TARGETPLATFORM", instructions.get(11).toCanonicalForm());
        assertEquals("TARGETPLATFORM", ((ArgInstruction) instructions.get(11)).getArgs().get(0).toCanonicalForm());

        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(12));
        assertEquals("RUN xx-apk add musl-dev gcc g++", instructions.get(12).toCanonicalForm());
        assertEquals("xx-apk add musl-dev gcc g++", ((RunInstruction) instructions.get(12)).getCommands().get(0));

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(13));
        assertEquals("Optionally set HUGO_BUILD_TAGS to \"none\" or \"withdeploy\" when building like so:\n" +
                "docker build --build-arg HUGO_BUILD_TAGS=withdeploy .\n" +
                "\n" +
                "We build the extended version by default.", ((CommentInstruction) instructions.get(13)).getComment());

        // ARG
        assertInstanceOf(ArgInstruction.class, instructions.get(14));
        assertEquals("ARG HUGO_BUILD_TAGS=\"extended\"", instructions.get(14).toCanonicalForm());
        assertEquals("HUGO_BUILD_TAGS=\"extended\"", ((ArgInstruction) instructions.get(14)).getArgs().get(0).toCanonicalForm());
        assertEquals("HUGO_BUILD_TAGS", ((ArgInstruction) instructions.get(14)).getArgs().get(0).getKey());
        assertEquals("extended", ((ArgInstruction) instructions.get(14)).getArgs().get(0).getValue());
        assertEquals(Quoting.DOUBLE_QUOTED, ((ArgInstruction) instructions.get(14)).getArgs().get(0).getQuoting());

        // ENV
        assertInstanceOf(EnvInstruction.class, instructions.get(15));
        assertEquals("ENV CGO_ENABLED=1", instructions.get(15).toCanonicalForm());
        assertEquals("CGO_ENABLED=1", ((EnvInstruction) instructions.get(15)).getVariables().get(0).toCanonicalForm());
        assertEquals("CGO_ENABLED", ((EnvInstruction) instructions.get(15)).getVariables().get(0).getKey());
        assertEquals("1", ((EnvInstruction) instructions.get(15)).getVariables().get(0).getValue());
        assertEquals(Quoting.UNQUOTED, ((EnvInstruction) instructions.get(15)).getVariables().get(0).getQuoting());

        assertInstanceOf(EnvInstruction.class, instructions.get(16));
        assertEquals("ENV GOPROXY=https://proxy.golang.org", instructions.get(16).toCanonicalForm());
        assertEquals("GOPROXY=https://proxy.golang.org", ((EnvInstruction) instructions.get(16)).getVariables().get(0).toCanonicalForm());
        assertEquals("GOPROXY", ((EnvInstruction) instructions.get(16)).getVariables().get(0).getKey());
        assertEquals("https://proxy.golang.org", ((EnvInstruction) instructions.get(16)).getVariables().get(0).getValue());
        assertEquals(Quoting.UNQUOTED, ((EnvInstruction) instructions.get(16)).getVariables().get(0).getQuoting());

        assertInstanceOf(EnvInstruction.class, instructions.get(17));
        assertEquals("ENV GOCACHE=/root/.cache/go-build", instructions.get(17).toCanonicalForm());
        assertEquals("GOCACHE=/root/.cache/go-build", ((EnvInstruction) instructions.get(17)).getVariables().get(0).toCanonicalForm());
        assertEquals("GOCACHE", ((EnvInstruction) instructions.get(17)).getVariables().get(0).getKey());
        assertEquals("/root/.cache/go-build", ((EnvInstruction) instructions.get(17)).getVariables().get(0).getValue());
        assertEquals(Quoting.UNQUOTED, ((EnvInstruction) instructions.get(17)).getVariables().get(0).getQuoting());

        assertInstanceOf(EnvInstruction.class, instructions.get(18));
        assertEquals("ENV GOMODCACHE=/go/pkg/mod", instructions.get(18).toCanonicalForm());
        assertEquals("GOMODCACHE=/go/pkg/mod", ((EnvInstruction) instructions.get(18)).getVariables().get(0).toCanonicalForm());
        assertEquals("GOMODCACHE", ((EnvInstruction) instructions.get(18)).getVariables().get(0).getKey());
        assertEquals("/go/pkg/mod", ((EnvInstruction) instructions.get(18)).getVariables().get(0).getValue());
        assertEquals(Quoting.UNQUOTED, ((EnvInstruction) instructions.get(18)).getVariables().get(0).getQuoting());

        // ARG
        assertInstanceOf(ArgInstruction.class, instructions.get(19));
        assertEquals("ARG TARGETPLATFORM", instructions.get(19).toCanonicalForm());
        assertEquals("TARGETPLATFORM", ((ArgInstruction) instructions.get(19)).getArgs().get(0).toCanonicalForm());

        // WORKDIR
        assertInstanceOf(WorkdirInstruction.class, instructions.get(20));
        assertEquals("WORKDIR /go/src/github.com/gohugoio/hugo", instructions.get(20).toCanonicalForm());
        assertEquals("/go/src/github.com/gohugoio/hugo", ((WorkdirInstruction) instructions.get(20)).getWorkdir());

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(21));
        assertEquals(
                "For  --mount=type=cache the value of target is the default cache id, so\n" +
                "for the go mod cache it would be good if we could share it with other Go images using the same setup,\n" +
                "but the go build cache needs to be per platform.\n" +
                "See this comment: https://github.com/moby/buildkit/issues/1706#issuecomment-702238282",
                ((CommentInstruction) instructions.get(21)).getComment());

        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(22));

        // TODO: solve this initial indent on set -ex which is currently dropped
        assertEquals("RUN --mount=target=. --mount=type=cache,target=/go/pkg/mod " +
                "--mount=type=cache,target=/root/.cache/go-build,id=go-build-$TARGETPLATFORM <<EOT\n" +
                "set -ex\n" +
                "    xx-go build -tags \"$HUGO_BUILD_TAGS\" -ldflags \"-s -w -X github.com/gohugoio/hugo/common/hugo.vendorInfo=docker\" -o /usr/bin/hugo\n" +
                "    xx-verify /usr/bin/hugo\n" +
                "EOT", instructions.get(22).toCanonicalForm());

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(23));
        assertEquals("dart-sass downloads the dart-sass runtime dependency", ((CommentInstruction) instructions.get(23)).getComment());

        // FROM
        assertInstanceOf(FromInstruction.class, instructions.get(24));
        assertEquals("FROM alpine:${ALPINE_VERSION} AS dart-sass", instructions.get(24).toCanonicalForm());
        assertEquals("alpine:${ALPINE_VERSION}", ((FromInstruction) instructions.get(24)).getImage());
        assertEquals("dart-sass", ((FromInstruction) instructions.get(24)).getAlias());

        // ARG
        assertInstanceOf(ArgInstruction.class, instructions.get(25));
        assertEquals("ARG TARGETARCH", instructions.get(25).toCanonicalForm());

        assertInstanceOf(ArgInstruction.class, instructions.get(26));
        assertEquals("ARG DART_SASS_VERSION", instructions.get(26).toCanonicalForm());

        assertInstanceOf(ArgInstruction.class, instructions.get(27));
        assertEquals("ARG DART_ARCH=${TARGETARCH/amd64/x64}", instructions.get(27).toCanonicalForm());

        // WORKDIR
        assertInstanceOf(WorkdirInstruction.class, instructions.get(28));
        assertEquals("WORKDIR /out", instructions.get(28).toCanonicalForm());

        // ADD
        assertInstanceOf(AddInstruction.class, instructions.get(29));
        assertEquals("ADD https://github.com/sass/dart-sass/releases/download/${DART_SASS_VERSION}/dart-sass-${DART_SASS_VERSION}-linux-${DART_ARCH}.tar.gz .", instructions.get(29).toCanonicalForm());
        assertEquals("https://github.com/sass/dart-sass/releases/download/${DART_SASS_VERSION}/dart-sass-${DART_SASS_VERSION}-linux-${DART_ARCH}.tar.gz", ((AddInstruction) instructions.get(29)).getSources().get(0));
        assertEquals(".", ((AddInstruction) instructions.get(29)).getDestination());

        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(30));
        assertEquals("RUN tar -xf dart-sass-${DART_SASS_VERSION}-linux-${DART_ARCH}.tar.gz", instructions.get(30).toCanonicalForm());

        // FROM
        assertInstanceOf(FromInstruction.class, instructions.get(31));
        assertEquals("FROM gorun AS final", instructions.get(31).toCanonicalForm());

        // COPY
        assertInstanceOf(CopyInstruction.class, instructions.get(32));
        assertEquals("COPY --from=build /usr/bin/hugo /usr/bin/hugo", instructions.get(32).toCanonicalForm());

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(33));
        assertEquals("libc6-compat  are required for extended libraries (libsass, libwebp).", ((CommentInstruction) instructions.get(33)).getComment());

        // TODO: Retain newline formatting for multi-line RUN strings with escapes?
        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(34));
        assertEquals("RUN apk add --no-cache libc6-compat git runuser nodejs npm", instructions.get(34).toCanonicalForm());

        // TODO: comments within RUN are extracted to preceding instructions. Should they be part of the RUN instruction?
        // This will only impact those who want to write back out the file, so not necessarily a problem atm.
        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(35));
        assertEquals("# For the Hugo's Git integration to work.", instructions.get(35).toCanonicalForm());

        assertInstanceOf(CommentInstruction.class, instructions.get(36));
        assertEquals("# See https://github.com/gohugoio/hugo/issues/9810", instructions.get(36).toCanonicalForm());

        // TODO: Retain newline formatting for multi-line RUN strings with escapes?
        // RUN
        assertInstanceOf(RunInstruction.class, instructions.get(37));
        assertEquals("mkdir -p /var/hugo/bin /cache && " +
                "addgroup -Sg 1000 hugo && " +
                "adduser -Sg hugo -u 1000 -h /var/hugo hugo && " +
                "chown -R hugo: /var/hugo /cache && runuser -u hugo -- git config --global --add safe.directory /project && " +
                "runuser -u hugo -- git config --global core.quotepath false",
                ((RunInstruction)instructions.get(37)).getCommands().get(0));

        // USER
        assertInstanceOf(UserInstruction.class, instructions.get(38));
        assertEquals("USER hugo:hugo", instructions.get(38).toCanonicalForm());
        assertEquals("hugo", ((UserInstruction) instructions.get(38)).getUser());
        assertEquals("hugo", ((UserInstruction) instructions.get(38)).getGroup());

        // VOLUME
        assertInstanceOf(VolumeInstruction.class, instructions.get(39));
        assertEquals("VOLUME /project", instructions.get(39).toCanonicalForm());
        assertEquals("/project", ((VolumeInstruction) instructions.get(39)).getVolume());

        // WORKDIR
        assertInstanceOf(WorkdirInstruction.class, instructions.get(40));
        assertEquals("WORKDIR /project", instructions.get(40).toCanonicalForm());
        assertEquals("/project", ((WorkdirInstruction) instructions.get(40)).getWorkdir());

        // ENV
        assertInstanceOf(EnvInstruction.class, instructions.get(41));
        assertEquals("ENV HUGO_CACHEDIR=/cache", instructions.get(41).toCanonicalForm());
        assertEquals("HUGO_CACHEDIR=/cache", ((EnvInstruction) instructions.get(41)).getVariables().get(0).toCanonicalForm());
        assertEquals("HUGO_CACHEDIR", ((EnvInstruction) instructions.get(41)).getVariables().get(0).getKey());
        assertEquals("/cache", ((EnvInstruction) instructions.get(41)).getVariables().get(0).getValue());
        assertEquals(Quoting.UNQUOTED, ((EnvInstruction) instructions.get(41)).getVariables().get(0).getQuoting());

        assertInstanceOf(EnvInstruction.class, instructions.get(42));
        assertEquals("ENV PATH=\"/var/hugo/bin:$PATH\"", instructions.get(42).toCanonicalForm());
        assertEquals("PATH=\"/var/hugo/bin:$PATH\"", ((EnvInstruction) instructions.get(42)).getVariables().get(0).toCanonicalForm());
        assertEquals("PATH", ((EnvInstruction) instructions.get(42)).getVariables().get(0).getKey());
        assertEquals("/var/hugo/bin:$PATH", ((EnvInstruction) instructions.get(42)).getVariables().get(0).getValue());
        assertEquals(Quoting.DOUBLE_QUOTED, ((EnvInstruction) instructions.get(42)).getVariables().get(0).getQuoting());

        // COPY
        assertInstanceOf(CopyInstruction.class, instructions.get(43));
        assertEquals("COPY scripts/docker/entrypoint.sh /entrypoint.sh", instructions.get(43).toCanonicalForm());
        assertEquals("scripts/docker/entrypoint.sh", ((CopyInstruction) instructions.get(43)).getSources().get(0));
        assertEquals("/entrypoint.sh", ((CopyInstruction) instructions.get(43)).getDestination());

        assertInstanceOf(CopyInstruction.class, instructions.get(44));
        assertEquals("COPY --from=dart-sass /out/dart-sass /var/hugo/bin/dart-sass", instructions.get(44).toCanonicalForm());
        assertEquals("dart-sass", ((CopyInstruction) instructions.get(44)).getFrom());
        assertEquals("/out/dart-sass", ((CopyInstruction) instructions.get(44)).getSources().get(0));
        assertEquals("/var/hugo/bin/dart-sass", ((CopyInstruction) instructions.get(44)).getDestination());

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(45));
        assertEquals("Update PATH to reflect the new dependencies.\n" +
                "For more complex setups, we should probably find a way to\n" +
                "delegate this to the script itself, but this will have to do for now.\n" +
                "Also, the dart-sass binary is a little special, other binaries can be put/linked\n" +
                "directly in /var/hugo/bin.", ((CommentInstruction) instructions.get(45)).getComment());

        // ENV
        assertInstanceOf(EnvInstruction.class, instructions.get(46));
        assertEquals("ENV PATH=\"/var/hugo/bin/dart-sass:$PATH\"", instructions.get(46).toCanonicalForm());
        assertEquals("PATH=\"/var/hugo/bin/dart-sass:$PATH\"", ((EnvInstruction) instructions.get(46)).getVariables().get(0).toCanonicalForm());
        assertEquals("PATH", ((EnvInstruction) instructions.get(46)).getVariables().get(0).getKey());
        assertEquals("/var/hugo/bin/dart-sass:$PATH", ((EnvInstruction) instructions.get(46)).getVariables().get(0).getValue());
        assertEquals(Quoting.DOUBLE_QUOTED, ((EnvInstruction) instructions.get(46)).getVariables().get(0).getQuoting());

        // COMMENT
        assertInstanceOf(CommentInstruction.class, instructions.get(47));
        assertEquals("Expose port for live server", ((CommentInstruction) instructions.get(47)).getComment());

        // EXPOSE
        assertInstanceOf(ExposeInstruction.class, instructions.get(48));
        assertEquals("EXPOSE 1313", instructions.get(48).toCanonicalForm());
        assertEquals("1313", ((ExposeInstruction) instructions.get(48)).getPorts().get(0).getPort());
        assertFalse(((ExposeInstruction) instructions.get(48)).getPorts().get(0).isProtocolProvided());
        assertEquals("tcp", ((ExposeInstruction) instructions.get(48)).getPorts().get(0).getProtocol());

        // ENTRYPOINT
        assertInstanceOf(EntrypointInstruction.class, instructions.get(49));
        assertEquals("ENTRYPOINT [\"/entrypoint.sh\"]", instructions.get(49).toCanonicalForm());
        assertEquals("/entrypoint.sh", ((EntrypointInstruction) instructions.get(49)).getEntrypoint().get(0));
        assertEquals(CommandInstruction.Form.EXEC, ((EntrypointInstruction) instructions.get(49)).getForm());

        // CMD
        assertInstanceOf(CmdInstruction.class, instructions.get(50));
        assertEquals("CMD [\"--help\"]", instructions.get(50).toCanonicalForm());
        assertEquals("--help", ((CmdInstruction) instructions.get(50)).getCommand().get(0));
        assertEquals(CommandInstruction.Form.EXEC, ((CmdInstruction) instructions.get(50)).getForm());
    }
}

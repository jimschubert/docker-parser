# docker-parser

A simple parser for Dockerfiles for Java 17+.
It can parse a Dockerfile and return a list of instructions, each representing a command in the Dockerfile.

## Why did I create this?

I have a pet project for implementing a rewrite-docker library for OpenRewrite. I initially tried implementing the parser
using Antlr4 but found that there were many nuances which would be easier to implement via manual parsing. This library
is a result of that use case.

## Features

- Parse Dockerfiles into a list of instructions
- Supports various Dockerfile commands such as `RUN`, `COPY`, `ADD`, `ENV`, `USER`, `VOLUME`, `WORKDIR`, `EXPOSE`, `ENTRYPOINT`, `CMD`, and comments
- Attempts to retain formatting and comments for accurate round-tripping (this is a work in progress)

## Installation

To include `docker-parser` in your project, use https://jitpack.io/ and then refer to a tagged version in GitHub. For example:

```
com.github.jimschubert:docker-parser:1.0.0
```

## Usage
Here's an example of how to use the parser:

```java
import com.github.jimschubert.docker.parser.DockerfileParser;
import com.github.jimschubert.docker.ast.DockerInstruction;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DockerfileParserExample {
    public static void main(String[] args) throws Exception {
        String dockerfileContent = new String(Files.readAllBytes(Paths.get("path/to/Dockerfile")));
        DockerfileParser parser = new DockerfileParser();
        List<DockerInstruction> instructions = parser.parse(dockerfileContent);

        for (DockerInstruction instruction : instructions) {
            System.out.println(instruction.toCanonicalForm());
        }
    }
}
```

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](./LICENSE) file for details.

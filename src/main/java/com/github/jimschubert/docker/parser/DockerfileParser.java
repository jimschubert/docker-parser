package com.github.jimschubert.docker.parser;

import com.github.jimschubert.docker.ast.*;
import com.github.jimschubert.docker.printer.ASTPrinter;
import com.github.jimschubert.docker.printer.DockerfilePrinter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a Dockerfile into a list of DockerInstruction objects.
 */
public class DockerfileParser {

    private final Map<String, String> envVariables = new HashMap<>();
    private final boolean expandVariables;
    private char escapeChar = '\\'; // Default escape character


    /**
     * Creates a new instance of DockerfileParser.
     */
    public DockerfileParser() {
        this(false);
    }

    /**
     * Creates a new instance of DockerfileParser.
     *
     * @param expandVariables Whether to expand environment variables in the Dockerfile.
     */
    public DockerfileParser(boolean expandVariables) {
        this.expandVariables = expandVariables;
    }

    /**
     * Parses a Dockerfile into a list of DockerInstruction objects.
     *
     * @param inputStream The input stream to read the Dockerfile from.
     * @return A list of DockerInstruction objects.
     * @throws IOException    If an I/O error occurs.
     * @throws ParserError If an error occurs while parsing the Dockerfile.
     */
    public List<DockerInstruction> parseDockerfile(InputStream inputStream) throws IOException, ParserError {
        List<DockerInstruction> instructions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        StringBuilder currentCommand = new StringBuilder();
        StringBuilder currentComment = new StringBuilder();
        boolean inHeredoc = false;
        String heredocDelimiter = null;

        while ((line = reader.readLine()) != null) {
            if (!inHeredoc) {
                // heredocs need to retain leading whitespace
                line = line.trim();
            }
            if (line.isEmpty()) {
                if (!currentComment.isEmpty()) {
                    instructions.add(new CommentInstruction(currentComment.toString().trim()));
                    currentComment = new StringBuilder();
                }
                continue;
            }

            if (inHeredoc) {
                currentCommand.append("\n").append(line);
                if (line.equals(heredocDelimiter)) {
                    inHeredoc = false;
                    instructions.add(parseInstruction(currentCommand.toString().trim()));
                    currentCommand = new StringBuilder();
                }
                continue;
            }

            if (line.startsWith("# syntax") || line.startsWith("# escape") || line.startsWith("# check")) {
                if (!currentComment.isEmpty()) {
                    instructions.add(new CommentInstruction(currentComment.toString().trim()));
                    currentComment = new StringBuilder();
                }
                if (line.startsWith("# escape")) {
                    escapeChar = line.charAt(line.length() - 1);
                }
                instructions.add(new DirectiveInstruction(line.substring(1).trim()));
                continue;
            } else if (line.startsWith("#")) {
                if (!currentComment.isEmpty()) {
                    currentComment.append("\n");
                }
                currentComment.append(line.substring(1).trim());
                continue;
            }

            if (!currentComment.isEmpty()) {
                instructions.add(new CommentInstruction(currentComment.toString().trim()));
                currentComment = new StringBuilder();
            }

            if (line.endsWith(String.valueOf(escapeChar))) {
                currentCommand.append(line, 0, line.length() - 1).append(" ");
                continue;
            } else {
                currentCommand.append(line);
            }

            Pattern heredocPattern = Pattern.compile("<<([A-Z]+)");
            Matcher heredocMatcher = heredocPattern.matcher(line);
            if (heredocMatcher.find()) {
                inHeredoc = true;
                heredocDelimiter = heredocMatcher.group(1);
                continue;
            }

            instructions.add(parseInstruction(currentCommand.toString().trim()));
            currentCommand = new StringBuilder();
        }

        if (!currentComment.isEmpty()) {
            instructions.add(new CommentInstruction(currentComment.toString().trim()));
        }

        return instructions;
    }

    private DockerInstruction parseInstruction(String line) throws ParserError {
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toUpperCase();
        String args = parts.length > 1 ? parts[1] : "";

        return switch (command) {
            case "ARG" -> parseArg(args);
            case "ADD" -> parseAdd(args);
            case "CMD" -> parseCmd(args);
            case "COPY" -> parseCopy(args);
            case "ENTRYPOINT" -> parseEntrypoint(args);
            case "ENV" -> parseEnv(args);
            case "EXPOSE" -> parseExpose(args);
            case "FROM" -> parseFrom(args);
            case "HEALTHCHECK" -> parseHealthCheck(args);
            case "LABEL" -> parseLabel(args);
            case "MAINTAINER" -> parseMaintainer(args);
            case "ONBUILD" -> parseOnBuild(args);
            case "RUN" -> parseRun(args);
            case "SHELL" -> parseShell(args);
            case "STOPSIGNAL" -> parseStopSignal(args);
            case "USER" -> parseUser(args);
            case "VOLUME" -> parseVolume(args);
            case "WORKDIR" -> parseWorkDir(args);
            default -> new DockerInstruction(command) {
                @Override
                public String toCanonicalForm() {
                    return command + " " + args;
                }
            };
        };
    }

    private ShellInstruction parseShell(String args) {
        List<String> commands = parseExecForm(args);
        return new ShellInstruction(commands);
    }

    private HealthCheckInstruction parseHealthCheck(String args) {
        if (args.trim().equalsIgnoreCase("NONE")) {
            return new HealthCheckInstruction();
        }

        HealthCheckInstruction.HealthCheckType type = HealthCheckInstruction.HealthCheckType.CMD;
        String test = null;
        String interval = null;
        String timeout = null;
        String startPeriod = null;
        String retries = null;

        String[] parts = args.split("\\s+", 2);
        if (parts.length > 1) {
            String[] options = parts[1].split("\\s+");
            for (String option : options) {
                String[] optionParts = option.split("=", 2);
                if (optionParts.length < 2) {
                    continue;
                }
                switch (optionParts[0].toLowerCase()) {
                    case "test" -> test = optionParts[1];
                    case "interval" -> interval = optionParts[1];
                    case "timeout" -> timeout = optionParts[1];
                    case "start-period" -> startPeriod = optionParts[1];
                    case "retries" -> retries = optionParts[1];
                }
            }
        }

        return new HealthCheckInstruction(type, test, interval, timeout, startPeriod, retries);
    }

    private StopSignalInstruction parseStopSignal(String args) {
        return new StopSignalInstruction(args);
    }

    private OnBuildInstruction parseOnBuild(String args) throws ParserError {
        DockerInstruction instruction = parseInstruction(args);

        if (instruction instanceof OnBuildInstruction) {
            throw new ParserError("Chaining ONBUILD instructions using ONBUILD ONBUILD isn't allowed.");
        }

        if (instruction instanceof FromInstruction || instruction instanceof MaintainerInstruction) {
            throw new ParserError("The ONBUILD instruction may not trigger FROM or MAINTAINER instructions.");
        }
        return new OnBuildInstruction(instruction);
    }

    private WorkdirInstruction parseWorkDir(String args) {
        return new WorkdirInstruction(args);
    }

    private UserInstruction parseUser(String args) {
        String[] parts = args.split(":", 2);
        String user = parts[0];
        String group = parts.length > 1 ? parts[1] : null;
        return new UserInstruction(user, group);
    }

    private DockerInstruction parseExpose(String args) {
        List<ExposeInstruction.Port> ports = parsePorts(args);
        return new ExposeInstruction(ports);
    }

    private AddInstruction parseAdd(String args) throws ParserError {
        List<String> sources = new ArrayList<>();
        String destination = null;
        Boolean keepGitDir = null;
        String checksum = null;
        String chown = null;
        String chmod = null;
        Boolean link = null;
        List<String> exclude = new ArrayList<>();

        Pattern optionPattern = Pattern.compile("--(keep-git-dir|checksum|chown|chmod|link|exclude)=?([^\\s]*)");
        Matcher optionMatcher = optionPattern.matcher(args);

        // Extract options
        while (optionMatcher.find()) {
            String option = optionMatcher.group(1);
            String value = optionMatcher.group(2);
            switch (option) {
                case "keep-git-dir" -> keepGitDir = value.isEmpty() || Boolean.parseBoolean(value);
                case "checksum" -> checksum = value;
                case "chown" -> chown = value;
                case "chmod" -> chmod = value;
                case "link" -> link = value.isEmpty() || Boolean.parseBoolean(value);
                case "exclude" -> exclude.add(value);
            }
        }

        // Remove options from args
        args = args.replaceAll("--(keep-git-dir|checksum|chown|chmod|link|exclude)=?[^\\s]*", "").trim();

        // Check if args are in JSON array format
        if (args.startsWith("[")) {
            List<String> parts = parseExecForm(args);
            if (parts.size() < 2) {
                throw new ParserError("ADD instruction requires at least one source and a destination");
            }
            destination = parts.remove(parts.size() - 1);
            sources.addAll(parts);
        } else {
            String[] parts = args.split("\\s+");
            if (parts.length < 2) {
                throw new ParserError("ADD instruction requires at least one source and a destination");
            }
            destination = parts[parts.length - 1];
            sources.addAll(Arrays.asList(parts).subList(0, parts.length - 1));
        }

        return new AddInstruction(sources, destination, keepGitDir, checksum, chown, chmod, link, exclude);
    }

    private CopyInstruction parseCopy(String args) throws ParserError {
        List<String> sources = new ArrayList<>();
        String destination = null;
        String from = null;
        String chown = null;
        String chmod = null;
        Boolean link = null;
        Boolean parents = null;
        List<String> exclude = new ArrayList<>();

        Pattern optionPattern = Pattern.compile("--(from|chown|chmod|link|parents|exclude)=?([^\\s]*)");
        Matcher optionMatcher = optionPattern.matcher(args);

        // Extract options
        while (optionMatcher.find()) {
            String option = optionMatcher.group(1);
            String value = optionMatcher.group(2);
            switch (option) {
                case "from" -> from = value;
                case "chown" -> chown = value;
                case "chmod" -> chmod = value;
                case "link" -> link = value.isEmpty() || Boolean.parseBoolean(value);
                case "parents" -> parents = value.isEmpty() || Boolean.parseBoolean(value);
                case "exclude" -> exclude.add(value);
            }
        }

        // Remove options from args
        args = args.replaceAll("--(from|chown|chmod|link|parents|exclude)=?[^\\s]*", "").trim();

        // Check if args are in JSON array format
        if (args.startsWith("[")) {
            List<String> parts = parseExecForm(args);
            if (parts.size() < 2) {
                throw new ParserError("COPY instruction requires at least one source and a destination");
            }
            destination = parts.remove(parts.size() - 1);
            sources.addAll(parts);
        } else {
            String[] parts = args.split("\\s+");
            if (parts.length < 2) {
                throw new ParserError("COPY instruction requires at least one source and a destination");
            }
            destination = parts[parts.length - 1];
            sources.addAll(Arrays.asList(parts).subList(0, parts.length - 1));
        }

        return new CopyInstruction(sources, destination, from, chown, chmod, link, parents, exclude);
    }

    private CmdInstruction parseCmd(String args) {
        CmdInstruction cmd = new CmdInstruction();
        parseCommand(cmd, args);
        return cmd;
    }

    private LabelInstruction parseLabel(String args) {
        List<KeyValuePair> labels = parseKeyValuePairs(args);
        return new LabelInstruction(labels);
    }

    private ArgInstruction parseArg(String args) {
        List<KeyValuePair> argsList = parseArgs(args);
        return new ArgInstruction(argsList);
    }

    private MaintainerInstruction parseMaintainer(String args) {
        return new MaintainerInstruction(args);
    }

    private EntrypointInstruction parseEntrypoint(String args) {
        EntrypointInstruction entrypoint = new EntrypointInstruction();
        parseCommand(entrypoint, args);
        return entrypoint;
    }

    private VolumeInstruction parseVolume(String args) {
        VolumeInstruction volume = new VolumeInstruction();
        parseCommand(volume, args);
        return volume;
    }

    private FromInstruction parseFrom(String args) {
        String platform = null;
        String image = null;
        String digest = null;
        String alias = null;

        Pattern platformPattern = Pattern.compile("--platform=([^\\s]+)");
        Matcher platformMatcher = platformPattern.matcher(args);
        if (platformMatcher.find()) {
            platform = platformMatcher.group(1);
            args = args.replace(platformMatcher.group(0), "").trim();
        }

        String[] parts = args.split("\\s+AS\\s+", 2);
        if (parts.length > 1) {
            alias = parts[1].trim();
        }

        String[] imageParts = parts[0].split("@", 2);
        image = imageParts[0].trim();
        if (imageParts.length > 1) {
            digest = imageParts[1].trim();
        }

        return new FromInstruction(platform, image, digest, alias);
    }

    private RunInstruction parseRun(String args) {
        List<String> commands = new ArrayList<>();
        List<RunInstruction.Mount> mounts = new ArrayList<>();
        RunInstruction.NetworkOption networkOption = null;
        RunInstruction.SecurityOption securityOption = null;
        String heredoc = null;

        Pattern mountTarget = Pattern.compile("--mount=target=([^ ]+)");
        Pattern mountPattern = Pattern.compile("--mount=type=([^,]+),target=([^,]+)(,id=([^\\s]+))?");
        Pattern networkPattern = Pattern.compile("--network=([^\\s]+)");
        Pattern securityPattern = Pattern.compile("--security=([^\\s]+)");
        Pattern heredocPattern = Pattern.compile("<<([A-Z]+)");

        String[] parts = args.split("\\s+");
        StringBuilder commandBuilder = new StringBuilder();
        boolean inHeredoc = false;
        String heredocName = "";

        for (String part : parts) {
            if (inHeredoc) {
                if (part.equals(heredocName)) {
                    inHeredoc = false;
                }
                continue;
            }

            Matcher mountTargetMatcher = mountTarget.matcher(part);
            if (mountTargetMatcher.find()) {
                mounts.add(new RunInstruction.Mount(mountTargetMatcher.group(1)));
                continue;
            }

            Matcher mountMatcher = mountPattern.matcher(part);
            if (mountMatcher.find()) {
                mounts.add(new RunInstruction.Mount(mountMatcher.group(1), mountMatcher.group(2), mountMatcher.group(4)));
                continue;
            }

            Matcher networkMatcher = networkPattern.matcher(part);
            if (networkMatcher.find()) {
                networkOption = RunInstruction.NetworkOption.valueOf(networkMatcher.group(1).toUpperCase());
                continue;
            }

            Matcher securityMatcher = securityPattern.matcher(part);
            if (securityMatcher.find()) {
                securityOption = RunInstruction.SecurityOption.valueOf(securityMatcher.group(1).toUpperCase());
                continue;
            }

            Matcher heredocMatcher = heredocPattern.matcher(part);
            if (heredocMatcher.find()) {
                inHeredoc = true;
                heredocName = heredocMatcher.group(1);
                // split args on the heredoc. This may be a bit of a hack, but it works.
                String[] heredocParts = args.split(heredocName);
                if (heredocParts.length > 1) {
                    // trim \r\n from start and end of heredoc

                    heredoc = heredocParts[1].trim();
                }
                continue;
            }

            if (!commandBuilder.isEmpty()) {
                commandBuilder.append(" ");
            }
            commandBuilder.append(part);
        }

        commands.add(commandBuilder.toString());

        return new RunInstruction(commands, mounts, networkOption, securityOption, heredoc, heredocName);
    }

    private EnvInstruction parseEnv(String args) {
        List<EnvVariable> vars = new ArrayList<>();
        Matcher matcher = Pattern.compile("(\\w+)(=|\\s)(.*)").matcher(args);
        while (matcher.find()) {
            String key = matcher.group(1);
            boolean deprecatedSyntax = " ".equals(matcher.group(2));
            String value = matcher.group(3);
            if (this.expandVariables) {
                value = expandVariables(value);
            }
            Quoting quoting = determineQuoting(value);
            if (quoting != Quoting.UNQUOTED) {
                value = value.substring(1, value.length() - 1);
            }
            vars.add(new EnvVariable(key, value, deprecatedSyntax, quoting));
            envVariables.put(key, value);
        }
        return new EnvInstruction(vars);
    }

    public static void parseCommand(CommandInstruction instruction, String args) {
        if (args.trim().startsWith("[")) {
            instruction.setForm(CommandInstruction.Form.EXEC);
            instruction.setCommand(parseExecForm(args));
        } else {
            instruction.setForm(CommandInstruction.Form.SHELL);
            instruction.setCommand(parseShellForm(args));
        }
    }

    public static List<String> parseExecForm(String command) {
        // Remove the surrounding brackets and split by comma
        String trimmed = command.substring(1, command.length() - 1).trim();
        String[] parts = trimmed.split("\\s*,\\s*");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            result.add(part.replaceAll("^\"|\"$", ""));
        }
        return result;
    }

    public static List<String> parseShellForm(String command) {
        return Arrays.asList(command.split("\\s+"));
    }

    private Quoting determineQuoting(String value) {
        if (value.startsWith("'") && value.endsWith("'")) {
            return Quoting.SINGLE_QUOTED;
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return Quoting.DOUBLE_QUOTED;
        } else {
            return Quoting.UNQUOTED;
        }
    }

    private List<KeyValuePair> parseArgs(String args) {
        List<KeyValuePair> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\w+)(=(\\S+))?");
        Matcher matcher = pattern.matcher(args);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(3) != null ? matcher.group(3) : "";
            Quoting quoteStyle = Quoting.UNQUOTED;
            if (value.indexOf("'") == 0 && value.lastIndexOf("'") == value.length() - 1) {
                value = value.substring(1, value.length() - 1);
                quoteStyle = Quoting.SINGLE_QUOTED;
            } else if(value.indexOf("\"") == 0 && value.lastIndexOf("\"") == value.length() - 1) {
                value = value.substring(1, value.length() - 1);
                quoteStyle = Quoting.DOUBLE_QUOTED;
            }
            result.add(new KeyValuePair(key, value, matcher.group(2) != null, quoteStyle));
        }
        return result;
    }

    private List<KeyValuePair> parseKeyValuePairs(String args) {
        List<KeyValuePair> labels = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\S+?)(=|\\s)(\"[^\"]*\"|'[^']*'|\\S+)");
        Matcher matcher = pattern.matcher(args);
        while (matcher.find()) {
            String key = matcher.group(1);
            String separator = matcher.group(2);
            String value = matcher.group(3);
            Quoting quoting = determineQuoting(value);
            if (quoting != Quoting.UNQUOTED) {
                value = value.substring(1, value.length() - 1);
            }
            boolean hasEquals = "=".equals(separator);
            labels.add(new KeyValuePair(key, value, hasEquals, quoting));
        }
        return labels;
    }

    private String expandVariables(String value) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            matcher.appendReplacement(sb, envVariables.getOrDefault(varName, ""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private List<ExposeInstruction.Port> parsePorts(String args) {
        List<ExposeInstruction.Port> ports = new ArrayList<>();
        String[] parts = args.split("\\s+");
        for (String part : parts) {
            String[] portParts = part.split("/", 2);
            if (portParts.length == 2) {
                ports.add(new ExposeInstruction.Port(portParts[0], portParts[1]));
            } else {
                ports.add(new ExposeInstruction.Port(portParts[0]));
            }
        }
        return ports;
    }

//    public static void main(String[] args) throws IOException, ParserError {
//        String dockerfileContent = """
//            # syntax=docker/dockerfile:1
//            # escape=\\
//            # check=skip=all
//            # check=error=true
//            # check=skip=JSONArgsRecommended,StageNameCasing
//            FROM openjdk:17 AS base
//            ENV APP_HOME=/app
//            # Copy Everything to HOME
//            # This is a multiline comment
//            COPY . ${APP_HOME}
//            VOLUME /src
//            RUN mkdir -p ${APP_HOME}/logs && \\
//                chown -R 1001:1001 ${APP_HOME}
//            CMD ["java", "-jar", "app.jar"]
//            """;
//        InputStream inputStream = new ByteArrayInputStream(dockerfileContent.getBytes());
//        DockerfileParser parser = new DockerfileParser();
//        List<DockerInstruction> parsedInstructions = parser.parseDockerfile(inputStream);
//
//        parsedInstructions.forEach(i -> System.out.println(ASTPrinter.toASTString(i)));
//
//        System.out.println("Dockerfile dumped: ");
//        DockerfilePrinter printer = new DockerfilePrinter(parsedInstructions);
//        System.out.println(printer.print());
//    }
}
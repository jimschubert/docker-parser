package com.github.jimschubert.docker.printer;

import com.github.jimschubert.docker.ast.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * A utility class to print an AST object as a string.
 */
public class ASTPrinter {

    /**
     * Converts an AST object to a string.
     * @param obj The AST object to convert.
     * @return The string representation of the AST object.
     */
    public static String toASTString(Object obj) {
        StringBuilder sb = new StringBuilder();
        printObject(obj, sb, 0);
        return sb.toString();
    }

    /**
     * Recursively prints an object and its fields.
     * @param obj The object to print.
     * @param sb The StringBuilder to append to.
     * @param indent The current indentation level.
     */
    private static void printObject(Object obj, StringBuilder sb, int indent) {
        if (obj == null) {
            sb.append("null");
            return;
        }

        if (obj instanceof String) {
            sb.append("\"").append(obj).append("\"");
            return;
        }

        Class<?> clazz = obj.getClass();
        sb.append(clazz.getSimpleName()).append(" {");

        // if class is KeyValuePair…
        if (clazz == KeyValuePair.class) {
            KeyValuePair pair = (KeyValuePair) obj;
            sb.append("\n").append("  ".repeat(indent + 1))
                    .append("key: ").append(pair.getKey()).append(", ")
                    .append("value: ").append(pair.getValue()).append(", ")
                    .append("hasEquals: ").append(pair.hasEquals()).append(", ")
                    .append("quoting: ").append(pair.getQuoting());
        } else if (clazz == EnvVariable.class) {
            EnvVariable env = (EnvVariable) obj;
            sb.append("\n").append("  ".repeat(indent + 1))
                    .append("key: ").append(env.getKey()).append(", ")
                    .append("value: ").append(env.getValue()).append(", ")
                    .append("deprecatedSyntax: ").append(env.isDeprecatedSyntax()).append(", ")
                    .append("quoting: ").append(env.getQuoting());
        } else if (clazz == CmdInstruction.class || clazz == VolumeInstruction.class || clazz == EntrypointInstruction.class) {
            CommandInstruction cmd = (CommandInstruction) obj;
            sb.append("\n").append("  ".repeat(indent + 1))
                    .append("form: ").append(cmd.getForm()).append(", ")
                    .append("command: ").append(cmd.getCommand());
        }

        else {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName().replace("val$", "");
                if (fieldName.equals("this$0")) {
                    continue;
                }
                try {
                    Object value = field.get(obj);
                    sb.append("\n").append("  ".repeat(indent + 1))
                            .append(fieldName).append(": ");

                    if (value instanceof Collection) {
                        sb.append("[");
                        for (Object item : (Collection<?>) value) {
                            sb.append("\n").append("  ".repeat(indent + 2));
                            printObject(item, sb, indent + 2);
                        }
                        sb.append("\n").append("  ".repeat(indent + 1)).append("]");
                    } else if (value instanceof String || value instanceof Character || value instanceof byte[]) {
                        sb.append("\"").append(value).append("\"");
                    } else if (value instanceof Number || value instanceof Boolean) {
                        sb.append(value);
                    } else if (value instanceof Enum<?>) {
                        sb.append(value.getClass().getSimpleName()).append(".").append(value);
                    } else if (value instanceof Map) {
                        sb.append("{");
                        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                            sb.append("\n").append("  ".repeat(indent + 2))
                                    .append(entry.getKey()).append(": ");
                            printObject(entry.getValue(), sb, indent + 2);
                        }
                        sb.append("\n").append("  ".repeat(indent + 1)).append("}");
                    } else {
                        sb.append(value);
                    }
                } catch (IllegalAccessException e) {
                    sb.append("ERROR");
                }
            }
        }
        sb.append("\n").append("  ".repeat(indent)).append("}");
    }
}
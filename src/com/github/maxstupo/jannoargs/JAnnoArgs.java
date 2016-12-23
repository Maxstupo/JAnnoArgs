package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * JAnnoArgs class handles the reading of program arguments and sets fields with the annotation {@link CmdArgument}.
 * 
 * @author Maxstupo
 */
public class JAnnoArgs {

    private static JAnnoArgs instance;

    private final Map<String, IArgumentEvent> events = new HashMap<>();

    private JAnnoArgs() {
    }

    public boolean registerEvent(String argumentName, IArgumentEvent evt) {

        if (events.containsKey(argumentName))
            return false;

        events.put(argumentName, evt);
        return true;
    }

    /**
     * Parse given string array, and set the fields that are annotated with a {@link CmdArgument} annotation from the given object. Supported data
     * types are<code> Boolean, String, Integer, Float, Double, Long </code> and the listed primitive counterparts.
     * <p>
     * 
     * Syntax:<br>
     * - Boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.<br>
     * - Value fields prefix the key with double hyphens (--) followed by a space and the value.
     * 
     * @param obj
     *            The object to look for fields and apply values to fields.
     * @param args
     *            The arguments to parse.
     * @return false if parsing failed.
     * @deprecated Use {@link #parseArguments(boolean, String, boolean, Object, String...)} instead.
     */
    @Deprecated
    public boolean parseArguments(Object obj, String... args) {
        return parseArguments(false, null, true, obj, args);
    }

    /**
     * Parse given string array, and set the fields that are annotated with a {@link CmdArgument} annotation from the given object. Supported data
     * types are<code> Boolean, String, Integer, Float, Double, Long </code> and the listed primitive counterparts.
     * <p>
     * 
     * Syntax:<br>
     * - Boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.<br>
     * - Value fields prefix the key with double hyphens (--) followed by a space and the value.
     * 
     * @param displayHelp
     *            If true generated help will be displayed if an error occured.
     * @param programDescription
     *            The description that will be used in the generated help. Set to null or empty to disable description.
     * @param displaySyntax
     *            If true the syntax will be displayed within the generated help.
     * @param obj
     *            The object to look for fields with {@link CmdArgument} annotation.
     * @param args
     *            The arguments to parse.
     * @return false if parsing failed.
     */
    public boolean parseArguments(boolean displayHelp, String programDescription, boolean displaySyntax, Object obj, String... args) {
        return parseArguments(displayHelp, programDescription, displaySyntax, obj, new Object[0], args);
    }

    /**
     * Parse given string array, and set the fields that are annotated with a {@link CmdArgument} annotation from the given object. Supported data
     * types are<code> Boolean, String, Integer, Float, Double, Long </code> and the listed primitive counterparts.
     * <p>
     * 
     * Syntax:<br>
     * - Boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.<br>
     * - Value fields prefix the key with double hyphens (--) followed by a space and the value.
     * 
     * @param displayHelp
     *            If true generated help will be displayed if an error occured.
     * @param programDescription
     *            The description that will be used in the generated help. Set to null or empty to disable description.
     * @param displaySyntax
     *            If true the syntax will be displayed within the generated help.
     * @param obj
     *            The object to look for fields with {@link CmdArgument} annotation.
     * @param ignoreObjs
     *            {@link CmdArgument CmdArguments} within the object will be ignored, and will prevent help being displayed.
     * @param args
     *            The arguments to parse.
     * @return false if parsing failed.
     */
    public boolean parseArguments(boolean displayHelp, String programDescription, boolean displaySyntax, Object obj, Object[] ignoreObjs, String... args) {
        Map<String, Field> keyToFields = createKeyToFieldsMap(obj);

        Map<String, Field> ignoreMap = new HashMap<>();
        for (Object o : ignoreObjs)
            ignoreMap.putAll(createKeyToFieldsMap(o));

        List<String> keys = new ArrayList<>();

        boolean failed = false;
        boolean assignNext = false;
        String key = null;

        for (String arg : args) {

            if (assignNext) {
                failed = !handleVariable(keyToFields, obj, key, arg);
                keys.add(key);
                assignNext = false;

            }

            if (arg.startsWith("--")) {
                key = arg.substring(2);

                if (ignoreMap.containsKey(key))
                    continue;

                assignNext = true;

            } else if (arg.startsWith("-")) {
                String tempKey = arg.substring(1);
                if (ignoreMap.containsKey(tempKey))
                    continue;

                failed = !handleBoolean(keyToFields, obj, tempKey, false);
                keys.add(tempKey);

            } else if (arg.startsWith("+")) {
                String tempKey = arg.substring(1);
                if (ignoreMap.containsKey(tempKey))
                    continue;

                failed = !handleBoolean(keyToFields, obj, tempKey, true);
                keys.add(tempKey);

            }

            if (failed)
                break;
        }

        if (failed) {
            if (displayHelp)
                System.out.println(generateHelp(programDescription, displaySyntax, obj));
            return false;
        }

        for (Entry<String, IArgumentEvent> entry : events.entrySet()) {

            if (!keys.contains(entry.getKey()))
                continue;
            entry.getValue().onEvent(entry.getKey());
        }
        return true;
    }

    private boolean handleBoolean(Map<String, Field> keyToFields, Object obj, String key, boolean state) {
        Field field = keyToFields.get(key);
        if (field != null && Util.isAssignable(field.getType(), boolean.class)) {
            try {
                field.setAccessible(true);
                field.set(obj, state);
                return true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean handleVariable(Map<String, Field> keyToFields, Object obj, String key, String value) {
        Field field = keyToFields.get(key);
        if (field != null) {
            Object objValue = convertStringToType(value, field.getType());
            if (objValue == null)
                return false;
            try {
                field.setAccessible(true);
                field.set(obj, objValue);
                return true;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private Object convertStringToType(String value, Class<?> type) {
        if (Util.isAssignable(type, String.class))
            return value;

        if (Util.isAssignable(type, int.class) && Util.isInt(value))
            return Integer.parseInt(value);

        if (Util.isAssignable(type, float.class) && Util.isFloat(value))
            return Float.parseFloat(value);

        if (Util.isAssignable(type, double.class) && Util.isDouble(value))
            return Double.parseDouble(value);

        if (Util.isAssignable(type, long.class) && Util.isLong(value))
            return Long.parseLong(value);

        return null;
    }

    /**
     * Gets all fields annotated with {@link CmdArgument} from the given object and maps them to the given {@link CmdArgument#key() key} from the
     * annotation.
     * 
     * @param obj
     *            The object to search for fields.
     * @return A map containing {@link Field fields} referenced by {@link CmdArgument#key() keys}.
     */
    public static Map<String, Field> createKeyToFieldsMap(Object obj) {
        Map<String, Field> keyToField = new HashMap<String, Field>();

        for (Field field : obj.getClass().getDeclaredFields()) {
            CmdArgument anno = field.getAnnotation(CmdArgument.class);
            if (anno == null)
                continue;
            keyToField.put(anno.key(), field);
        }

        return keyToField;
    }

    /**
     * Prints all fields in the given object annotated with a {@link CmdArgument} annotation with format of [{@link CmdArgument#key() key}: (fieldName
     * = fieldValue)]
     * 
     * @param obj
     *            The object to look for fields.
     */
    public static void printCmdArgumentFields(Object obj) {
        Map<String, Field> keyToFields = createKeyToFieldsMap(obj);

        for (Entry<String, Field> entry : keyToFields.entrySet()) {
            try {
                Field field = entry.getValue();
                field.setAccessible(true);

                System.out.println(entry.getKey() + ": ('" + entry.getValue().getName() + "' = '" + field.get(obj) + "')");

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates a formatted string containing the arguments available, from the specified objects.
     * 
     * @param programDescription
     *            a description that is prefixed to the start of the help string.
     * @param displaySyntax
     *            if true the syntax of how to set argument values will be added to help string.
     * @param classObjects
     *            The objects that the {@link CmdArgument} annotations will be extracted from, using {@link #createKeyToFieldsMap(Object)}.
     * @return a formatted help string ready to be printed to the console.
     * @see CmdArgument
     * @see #createKeyToFieldsMap(Object)
     */
    public static String generateHelp(String programDescription, boolean displaySyntax, Object... classObjects) {
        StringBuilder sb = new StringBuilder();

        Map<String, Field> keyToFields = new TreeMap<>();
        for (Object classObject : classObjects)
            keyToFields.putAll(createKeyToFieldsMap(classObject));

        // Find the maximum key length for all.
        int maxKeyLength = 0;
        for (Field field : keyToFields.values()) {
            CmdArgument argument = field.getAnnotation(CmdArgument.class);
            if (argument.hide())
                continue;

            String key = argument.key();
            maxKeyLength = Math.max(maxKeyLength, key.length());
        }

        // Construct the help.
        if (programDescription != null && !programDescription.isEmpty())
            sb.append(programDescription).append("\n");

        for (Field field : keyToFields.values()) {
            CmdArgument argument = field.getAnnotation(CmdArgument.class);
            if (argument.hide())
                continue;

            String key = argument.key();
            String desc = argument.desc();

            // Get prefix if boolean use '+/-' otherwise use '--'
            String prefix = "--";
            if (Util.isAssignable(field.getType(), boolean.class))
                prefix = "+/-";

            sb.append(String.format("  %3s%-" + maxKeyLength + "s         %s", prefix, key, desc));
            sb.append("\n");
        }

        if (displaySyntax) {
            sb.append("\n").append("Syntax:").append("\n");
            sb.append("  - Boolean fields: Prefix the key with plus(+) for true, or prefix a hyphen(-) for false.").append("\n");
            sb.append("  - Value fields: Prefix the key with double hyphens (--) followed by a space and the value.").append("\n");
        }
        return sb.toString();
    }

    public static final JAnnoArgs get() {
        if (instance == null)
            instance = new JAnnoArgs();
        return instance;
    }

}

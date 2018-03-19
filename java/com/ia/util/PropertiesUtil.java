package com.ia.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class PropertiesUtil {
    private static final String TEMPLATE = "\t%s = %s";

    public static void main(final String[] args) throws IOException {
        final PropertiesUtil util = new PropertiesUtil();
        final Properties template = util.loadProperties(args[0]);
        final Properties actual = util.loadProperties(args[1]);

        final Map<String, String> missing = util.getMissingProperties(template, actual);
        if (missing.size() > 0) {
            System.out.println("\nProperties that are required but missing:");
            util.prettyPrint(missing, System.out);
        }

        final Map<String, String> unused = util.getUnusedProperties(template, actual);
        if (unused.size() > 0) {
            System.out.println("\nProperties that are no longer used:");
            util.prettyPrint(unused, System.out);
        }

        /* Exit with error if there any properties are missing. */
        if (missing.size() > 0) {
            System.exit(1);
        }
    }

    private Map<String, String> getMissingProperties(final Properties template,
            final Properties actual) {
        final Map<String, String> missing = new HashMap<String, String>();

        for (final Object key : template.keySet()) {
            if (!actual.containsKey(key)) {
                missing.put((String) key, template.getProperty((String) key));
            }
        }

        return missing;
    }

    private Map<String, String> getUnusedProperties(final Properties template,
            final Properties actual) {
        final Map<String, String> unused = new HashMap<String, String>();

        for (final Object key : actual.keySet()) {
            if (!template.containsKey(key)) {
                unused.put((String) key, actual.getProperty((String) key));
            }
        }

        return unused;
    }

    private Properties loadProperties(final String filename) throws IOException {
        final Properties properties = new Properties();
        final FileInputStream inStream = new FileInputStream(filename);
        properties.load(inStream);
        inStream.close();
        return properties;
    }

    private void prettyPrint(final Map<String, String> delta, final PrintStream out) {
        for (final Map.Entry<String, String> me : delta.entrySet()) {
            out.println(String.format(TEMPLATE, me.getKey(), me.getValue()));
        }
    }
}

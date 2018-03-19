package com.ia.util;

import static com.ia.common.IAProperties.templateBaseDir;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public final class KUtilities {

    private static final String CHARSETALPHANUM =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static SecureRandom rand = new SecureRandom();

    public static String generateRandomString(final int length) {
        final StringBuilder sb = new StringBuilder(length);
        final String charSet = CHARSETALPHANUM;
        while (sb.length() < length) {
            sb.append(charSet.charAt(rand.nextInt(charSet.length())));
        }

        // Prefix token with P so that it can be filtered.

        return "P" + sb.toString().toUpperCase();
    }

    /**
     * Returns a 30-bit pseudorandom, guranteed to be always positive integer.
     */
    public static int getRandomInt() {
        return rand.nextInt(1 << 30);
    }

    /**
     * Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the
     * specified value (exclusive), drawn from this random number generator's sequence.
     */
    public static int getRandomInt(final int n) {
        return rand.nextInt(n);
    }

    public static VelocityEngine getVelocityEngine(final String organisation) throws Exception {
        final String templateDir = templateBaseDir;
        final Properties properties = getVelocityProperties(templateDir);
        return new VelocityEngine(properties);
    }

    public static VelocityEngine getVelocityEngineForTemplateDir(final String templatesDirectory)
            throws Exception {
        final Properties properties = getVelocityProperties(templatesDirectory);
        return new VelocityEngine(properties);
    }

    private static Properties getVelocityProperties(final String templatesDirectory) {
        final Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        properties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatesDirectory);

        return properties;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] makeArray(final Class<T> klass, final int size, final T defaultValue) {
        final T[] array = (T[]) Array.newInstance(klass, size);
        for (int index = 0; index < array.length; index++) {
            array[index] = defaultValue;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] makeArray(final T... elements) {
        return elements;
    }
}

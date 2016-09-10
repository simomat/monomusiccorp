package de.infonautika.monomusiccorp.app.util;

public class Strings {

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean startsWithIgnoreCase(String string, String prefix)
    {
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}

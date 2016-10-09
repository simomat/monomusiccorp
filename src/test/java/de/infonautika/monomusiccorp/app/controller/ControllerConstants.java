package de.infonautika.monomusiccorp.app.controller;

public class ControllerConstants {
    public static final String HTTP_LOCALHOST = "http://localhost";
    public static final String LINKS_SELF_HREF = ".links[?(@.rel=='self')].href";


    public static String linkOfRel(String rel) {
        return ".links[?(@.rel=='" + rel + "')].href";
    }

    public static String linkOfSelf() {
        return LINKS_SELF_HREF;
    }
}

package de.infonautika.monomusiccorp.app.util;

public enum ResultStatus {
    OK, USER_EXISTS;

    public static boolean isOk(ResultStatus resultStatus) {
        return OK.equals(resultStatus);
    }
}

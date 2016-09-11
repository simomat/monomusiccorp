package de.infonautika.monomusiccorp.app.controller;

public enum ResultStatus {
    OK, USER_EXISTS, NO_CUSTOMER;

    public static boolean isOk(ResultStatus resultStatus) {
        return OK.equals(resultStatus);
    }
}

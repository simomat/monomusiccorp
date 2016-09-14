package de.infonautika.monomusiccorp.app.business;

public enum ResultStatus {
    OK, USER_EXISTS, NO_CUSTOMER, NOT_EXISTENT;

    public static boolean isOk(ResultStatus resultStatus) {
        return OK.equals(resultStatus);
    }
}

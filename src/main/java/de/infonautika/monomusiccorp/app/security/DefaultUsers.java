package de.infonautika.monomusiccorp.app.security;

public class DefaultUsers {
    public static final MutableUserDetails ADMIN = MutableUserDetails.create("admin", "admin", UserRole.ADMIN, UserRole.STOCK_MANAGER);
}

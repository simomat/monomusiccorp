package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.provisioning.UserDetailsManager;

public interface ModifiableUserDetailsManager extends UserDetailsManager{
    void deleteUsers();
}

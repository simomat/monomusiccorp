package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.domain.ConflictException;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    void addUser(UserDetails customer) throws ConflictException;

    void deleteUsers();
}

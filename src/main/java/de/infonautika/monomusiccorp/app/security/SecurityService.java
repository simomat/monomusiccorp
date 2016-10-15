package de.infonautika.monomusiccorp.app.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    void addUser(UserDetails customer) ;

    void deleteUsers();
}

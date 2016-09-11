package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.controller.ResultStatus;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    ResultStatus addUser(UserDetails customer);

    void deleteUsers();
}

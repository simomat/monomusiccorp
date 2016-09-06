package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.util.ResultStatus;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityService {
    ResultStatus addUser(UserDetails customer);
}

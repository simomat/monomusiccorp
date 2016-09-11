package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.controller.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService{

    @Autowired
    private ModifiableUserDetailsManager userDetailsManager;

    @Override
    public ResultStatus addUser(UserDetails customer) {
        if (userDetailsManager.userExists(customer.getUsername())) {
            return ResultStatus.USER_EXISTS;
        }
        userDetailsManager.createUser(customer);
        return ResultStatus.OK;
    }

    @Override
    public void deleteUsers() {
        userDetailsManager.deleteUsers();
    }
}

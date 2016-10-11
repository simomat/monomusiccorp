package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.domain.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService{

    final Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

    @Autowired
    private ModifiableUserDetailsManager userDetailsManager;

    @Override
    public void addUser(UserDetails userDetails) throws ConflictException {
        if (userDetailsManager.userExists(userDetails.getUsername())) {
            logger.info("failed to add user {} because user exists", userDetails.getUsername());
            throw new ConflictException("user '" + userDetails.getUsername() + "' already exists");
        }
        userDetailsManager.createUser(userDetails);
        logger.info("added user {}", userDetails.getUsername());
    }

    @Override
    public void deleteUsers() {
        userDetailsManager.deleteUsers();
        logger.info("deleted all Users");
    }
}

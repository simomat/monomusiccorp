package de.infonautika.monomusiccorp.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ModifiableUserDetailsManagerImpl implements ModifiableUserDetailsManager {

    private final Map<String, MutableUserDetails> users = new HashMap<>();

    @Autowired
    private AuthenticationFacade authenticationFacade;


    @Override
    public void createUser(UserDetails user) {
        requireNonNull(user);
        requireNonNull(user.getUsername());
        requireNonNull(user.getAuthorities());

        Assert.isTrue(!userExists(user.getUsername()));

        putUser(user);
    }

    private void putUser(UserDetails user) {
        users.put(user.getUsername().toLowerCase(), new MutableUserDetails(user));
    }

    @Override
    public void updateUser(UserDetails user) {
        Assert.isTrue(userExists(user.getUsername()));
        putUser(user);
    }

    @Override
    public void deleteUser(String username) {
        users.remove(username.toLowerCase());
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Optional<String> currentUserName = authenticationFacade.getCurrentUserName();

        if (!currentUserName.isPresent()) {
            throw new AccessDeniedException("no current user.");
        }

        MutableUserDetails user = users.get(currentUserName.get().toLowerCase());

        if (user == null) {
            throw new IllegalStateException("Current user doesn't exist.");
        }

        user.setPassword(newPassword);
    }


    @Override
    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MutableUserDetails userDetails = users.get(username.toLowerCase());
        if (userDetails == null) {
            throw new UsernameNotFoundException(username);
        }
        return userDetails;
    }

    @Override
    public void deleteUsers() {
        users.clear();
    }
}

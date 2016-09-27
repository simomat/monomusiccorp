package de.infonautika.monomusiccorp.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModifiableUserDetailsManagerImpl implements ModifiableUserDetailsManager {

    private final Map<String, MutableUserDetails> users = new HashMap<>();

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public void createUser(UserDetails user) {
        Assert.notNull(user);
        Assert.notNull(user.getUsername());
        Assert.notNull(user.getAuthorities());

        Assert.isTrue(!userExists(user.getUsername()));

        putUser(user);
    }

    private void putUser(UserDetails user) {
        users.put(toUserKey(user.getUsername()), new MutableUserDetails(user));
    }

    @Override
    public void updateUser(UserDetails user) {
        Assert.isTrue(userExists(user.getUsername()));
        putUser(user);
    }

    @Override
    public void deleteUser(String userName) {
        users.remove(toUserKey(userName));
    }

    private static String toUserKey(String userName) {
        return userName.toLowerCase();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        authenticationFacade.getCurrentUserName()
                .map(userName ->
                        getUser(userName)
                            .map(user -> user.setPassword(newPassword))
                            .orElseGet(() -> {
                                throw new IllegalStateException("Current user doesn't exist.");
                            }))
                .orElseGet(() -> {
                    throw new AccessDeniedException("no current user.");
                });
    }

    private Optional<MutableUserDetails> getUser(String userName) {
        return Optional.ofNullable(users.get(toUserKey(userName)));
    }

    @Override
    public boolean userExists(String userName) {
        return getUser(userName).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return getUser(userName)
                .orElseGet(() -> {
                    throw new UsernameNotFoundException(userName);
                });
    }

    @Override
    public void deleteUsers() {
        users.clear();
    }
}

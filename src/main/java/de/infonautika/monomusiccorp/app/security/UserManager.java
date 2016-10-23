package de.infonautika.monomusiccorp.app.security;

import de.infonautika.monomusiccorp.app.domain.User;
import de.infonautika.monomusiccorp.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserManager implements ModifiableUserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Override
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @Override
    public void createUser(UserDetails userDetails) {
        userRepository.save(fromUserDetails(userDetails));
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        userRepository.save(fromUserDetails(userDetails));
    }

    @Override
    public void deleteUser(String s) {
        userRepository.delete(s);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = currentUser();
        if (user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.exists(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("user " + username + " not found");
    }

    private User fromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof User) {
            return (User) userDetails;
        }

        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setAuthorities(userDetails.getAuthorities());
        user.setPassword(userDetails.getPassword());
        return user;
    }

    private User currentUser() {
        return authenticationFacade.getCurrentUserName()
                .map(userName -> {
                    Optional<User> user = userRepository.findByUsername(userName);
                    if (user.isPresent()) {
                        return user.get();
                    }
                    throw new IllegalStateException("logged in user is not a valid user");
                })
                .orElseThrow(() -> new IllegalStateException("no current user"));
    }
}

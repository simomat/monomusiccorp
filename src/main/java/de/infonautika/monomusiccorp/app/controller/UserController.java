package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping("/currentuser")
    public String currentUser() {
        return authenticationFacade.getCurrentUserName()
                .orElse("Unknown User");
    }

}

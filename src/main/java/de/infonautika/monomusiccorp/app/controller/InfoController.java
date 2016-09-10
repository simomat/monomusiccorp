package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping("/currentuser")
    public String currentUser() {
        User principal = (User) authenticationFacade.getAuthentication().getPrincipal();
        return principal.getUsername();
    }

}

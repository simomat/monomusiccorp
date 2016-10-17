package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping(value = "/currentuser", method = RequestMethod.GET)
    @Relation("user")
    public ResponseEntity currentUser() {
        return authenticationFacade.getCurrentUserName()
                .map(username -> {
                    Resource<String> resource = new Resource<>(username);
                    resource.add(linkOn(methodOn(getClass()).currentUser()).withRelSelf());

                    return ResponseEntity.ok(resource);
                })
                .orElseThrow(() -> new DoesNotExistException("current user not available"));
    }

}

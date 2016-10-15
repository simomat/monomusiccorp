package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.invocationOf;
import static de.infonautika.monomusiccorp.app.controller.utils.links.InvocationProxy.methodOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkCreator.createLink;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping("/currentuser")
    public ResponseEntity currentUser() {
        return authenticationFacade.getCurrentUserName()
                .map(username -> {
                    Resource<String> resource = new Resource<>(username);
                    resource.add(createLink(invocationOf(methodOn(getClass()).currentUser())).withRelSelf());

                    return ResponseEntity.ok(resource);
                })
                .orElseThrow(() -> new DoesNotExistException("current user not available"));
    }

}

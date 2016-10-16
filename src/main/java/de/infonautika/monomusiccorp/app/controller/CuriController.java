package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.utils.links.RelationMethodRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;


@Controller
@RequestMapping("/api/curis")
public class CuriController {

    @Autowired
    private RelationMethodRegistry relationMethodRegistry;

    @RequestMapping(value = "/{rel}", method = RequestMethod.GET)
    public ResponseEntity currentUser(@PathVariable("rel") String relationName) {
        return relationMethodRegistry.getCuri(relationName).map(ResponseEntity::ok)
                .orElseGet(notFound());
    }

}

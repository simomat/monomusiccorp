package de.infonautika.monomusiccorp.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.infonautika.monomusiccorp.app.controller.utils.links.curi.CuriInfo;
import de.infonautika.monomusiccorp.app.controller.utils.links.curi.MethodCuriProvider;
import de.infonautika.monomusiccorp.app.controller.utils.links.curi.RelationMethodRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@RequestMapping("/api/curis")
public class CuriController {

    @Autowired
    private RelationMethodRegistry relationMethodRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MethodCuriProvider methodCuriProvider;

    @RequestMapping(value = "/{rel}", method = RequestMethod.GET)
    public ResponseEntity currentUser(@PathVariable("rel") String relationName) {

        return relationMethodRegistry.getCuri(relationName)
                .map(methodCuriProvider::toCuriInfo)
                .map(this::format)
                .map(ResponseEntity::ok)
                .orElseThrow(ResourceNotFoundException::new);
    }

    private String format(CuriInfo curiInfo) {
        try {
            return "<pre>" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(curiInfo) + "</pre>";
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
    }
}

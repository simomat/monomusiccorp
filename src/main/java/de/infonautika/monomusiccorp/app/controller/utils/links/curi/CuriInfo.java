package de.infonautika.monomusiccorp.app.controller.utils.links.curi;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;
import java.util.Map;

public class CuriInfo {
    private String uri;
    private final Collection<RequestMethod> methods;
    private final Map<String, JsonSchema> pathVariables;
    private final Map<String, JsonSchema> requestParameters;
    private final JsonSchema bodySchema;

    public CuriInfo(String uri, Collection<RequestMethod> methods, Map<String, JsonSchema> pathVariables, Map<String, JsonSchema> requestParameters, JsonSchema bodySchema) {
        this.uri = uri;
        this.methods = methods;
        this.pathVariables = pathVariables;
        this.requestParameters = requestParameters;
        this.bodySchema = bodySchema;
    }

    public String getUri() {
        return uri;
    }

    public Collection<RequestMethod> getMethods() {
        return methods;
    }

    public Map<String, JsonSchema> getPathVariables() {
        return pathVariables;
    }

    public Map<String, JsonSchema> getRequestParameters() {
        return requestParameters;
    }

    public JsonSchema getBodySchema() {
        return bodySchema;
    }
}

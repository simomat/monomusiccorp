package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.hateoas.*;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Consumer;

public class LinkSupport {

    private static final MappingDiscoverer MAPPING_DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);
    public static final String SELF = "self";

    public static Consumer<Invocation> addLink(ResourceSupport resource, String relationName) {
        return invocation -> resource.add(createLink(relationName, invocation));
    }

    public static Consumer<Invocation> addTemplateLink(ResourceSupport resource, String relationName, TemplateVariable... templateVariables) {
        TemplateVariables variables = new TemplateVariables(templateVariables);
        return invocation -> resource.add(createLink(relationName, invocation, variables));
    }

    public static Link createLink(String relationName, Invocation invocation) {
        return createLink(relationName, new UriTemplate(getMapping(invocation)));
    }

    public static Link createSelfLink(Invocation invocationAware) {
        return createLink(SELF, invocationAware);
    }

    private static Link createLink(String relationName, Invocation invocation, TemplateVariables variables) {
        return createLink(
                relationName,
                new UriTemplate(
                    getMapping(invocation),
                    variables));
    }

    public static Link createSelfLink(Class<?> clazz) {
        return createLink(SELF, new UriTemplate(getMapping(clazz)));
    }

    private static String getMapping(Class<?> clazz) {
        return MAPPING_DISCOVERER.getMapping(clazz);
    }

    private static Link createLink(String relationName, UriTemplate uriTemplate) {
        return new Link(uriTemplate, relationName);
    }

    private static String getMapping(Invocation invocation) {
        return MAPPING_DISCOVERER.getMapping(invocation.getLastInvocation().getMethod());
    }

    public static TemplateVariable pathTemplateVariable(String name) {
        return pathTemplateVariable(name, "");
    }

    public static TemplateVariable pathTemplateVariable(String name, String description) {
        return new TemplateVariable(name, TemplateVariable.VariableType.PATH_VARIABLE, description);
    }

}

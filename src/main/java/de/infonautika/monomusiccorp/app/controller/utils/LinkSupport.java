package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.hateoas.*;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Consumer;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class LinkSupport {

    private static final MappingDiscoverer MAPPING_DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);

    public static Consumer<DummyInvocationUtils.LastInvocationAware> addLink(ResourceSupport resource, String relationName) {
        return invocationAware -> resource.add(linkTo(invocationAware).withRel(relationName));
    }

    public static Consumer<DummyInvocationUtils.LastInvocationAware> addTemplateLink(ResourceSupport resource, String relationName, TemplateVariable... templateVariables) {
        TemplateVariables variables = new TemplateVariables(templateVariables);

        // Somehow, ControllerLinkBuilder does not give the option to add TemplateVariables to build a
        // template link, does it?
        // Maybe this will be provided in future versions
        return invocationAware -> resource.add(
            new Link(
                new UriTemplate(
                        MAPPING_DISCOVERER.getMapping(invocationAware.getLastInvocation().getMethod()),
                        variables),
                relationName));
    }

    public static TemplateVariable pathTemplateVariable(String name) {
        return new TemplateVariable(name, TemplateVariable.VariableType.PATH_VARIABLE, "");
    }

    public static TemplateVariable pathTemplateVariable(String name, String description) {
        return new TemplateVariable(name, TemplateVariable.VariableType.PATH_VARIABLE, description);
    }
}

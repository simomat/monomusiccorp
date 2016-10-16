package de.infonautika.monomusiccorp.app.controller.utils;

import de.infonautika.monomusiccorp.app.controller.utils.links.Invocation;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.function.Consumer;

import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;

public class LinkSupport {

    private static final MappingDiscoverer MAPPING_DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);

    public static Consumer<Invocation> addSelfLink(ResourceSupport resource) {
        return invocation -> resource.add(linkOn(invocation).withRelSelf());
    }

    public static Consumer<Invocation> addLink(ResourceSupport resource) {
        return invocation -> resource.add(linkOn(invocation).withGivenRel());
    }

    public static Link createSelfLink(Class<?> clazz) {
        return new Link(new UriTemplate(MAPPING_DISCOVERER.getMapping(clazz)), Link.REL_SELF);
    }
}

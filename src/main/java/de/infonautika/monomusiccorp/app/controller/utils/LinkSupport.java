package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.DummyInvocationUtils;

import java.util.function.Consumer;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class LinkSupport {

    public static Consumer<DummyInvocationUtils.LastInvocationAware> addLink(ResourceSupport resource, String relationName) {
        return invocationAware -> resource.add(linkTo(invocationAware).withRel(relationName));
    }

}

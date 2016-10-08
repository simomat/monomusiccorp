package de.infonautika.monomusiccorp.app.controller;

import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public interface SelfLinkSupplier {

    default void addSelfLink(Resources<?> resources) {
        resources.add(linkTo(getClass()).withSelfRel());
    }

}

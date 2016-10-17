package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.hateoas.ResourceSupport;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.createSelfLink;

public interface SelfLinkSupplier {

    default void addSelfLink(ResourceSupport resource) {
        resource.add(createSelfLink(getClass()));
    }

}
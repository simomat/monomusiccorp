package de.infonautika.monomusiccorp.app.controller.utils.links;

import org.springframework.util.Assert;

public class LinkFacade {
    public static LinkCreator linkOn(Object invocation) {
        Assert.isInstanceOf(Invocation.class, invocation);
        return new LinkCreator((Invocation) invocation);
    }

    public static <T> T methodOn(Class<T> type) {
        return InvocationProxy.methodOn(type);
    }
}

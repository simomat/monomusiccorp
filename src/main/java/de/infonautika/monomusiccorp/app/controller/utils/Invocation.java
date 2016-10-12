package de.infonautika.monomusiccorp.app.controller.utils;

import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.util.Assert;

import java.util.Iterator;

public class Invocation implements DummyInvocationUtils.LastInvocationAware {

    private final DummyInvocationUtils.LastInvocationAware inner;

    private Invocation(DummyInvocationUtils.LastInvocationAware invocation) {
        inner =  invocation;
    }

    public static <T> T methodOn(Class<T> controller) {
        return DummyInvocationUtils.methodOn(controller);
    }

    @Override
    public Iterator<Object> getObjectParameters() {
        return inner.getObjectParameters();
    }

    @Override
    public DummyInvocationUtils.MethodInvocation getLastInvocation() {
        return inner.getLastInvocation();
    }

    public static Invocation invocationOf(Object invocation) {
        Assert.isInstanceOf(DummyInvocationUtils.LastInvocationAware.class, invocation);
        return new Invocation((DummyInvocationUtils.LastInvocationAware)invocation);
    }
}

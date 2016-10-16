package de.infonautika.monomusiccorp.app.controller.utils.links;

import java.lang.reflect.Method;

public interface Invocation {

    Object[] getArguments();

    Method getMethod();

}

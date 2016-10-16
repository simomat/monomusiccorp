package de.infonautika.monomusiccorp.app.controller.utils.links;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.AnnotationMappingDiscoverer;
import org.springframework.hateoas.core.MappingDiscoverer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class LinkCreator {

    private static final MappingDiscoverer MAPPING_DISCOVERER = new AnnotationMappingDiscoverer(RequestMapping.class);
    private static final String PATHVARSTART = "PATHVARSTART-";
    private static final String PATHVAREND = "-PATHVAREND";
    private static final String REPLACE_PATTERN = PATHVARSTART + "(.+?)" + PATHVAREND;

    private Invocation invocation;

    LinkCreator(Invocation invocation) {
        this.invocation = invocation;
    }

    public Link withRel(String relationName) {
        return new Link(getHref(), relationName);
    }

    public Link withRelSelf() {
        return withRel(Link.REL_SELF);
    }

    String getHref() {
        return getUri().toString().replaceAll(REPLACE_PATTERN, "{$1}");
    }

    private URI getUri() {
        return getUriComponents().encode().toUri();
    }

    private UriComponents getUriComponents() {
        UriComponentsBuilder uriComponentsBuilder = bindRequestParameters(getUriComponentsBuilder());
        return bindPathVariables(uriComponentsBuilder);
    }

    private UriComponentsBuilder getUriComponentsBuilder() {
        return UriComponentsBuilder.fromPath(MAPPING_DISCOVERER.getMapping(invocation.getMethod()));
    }

    private UriComponentsBuilder bindRequestParameters(UriComponentsBuilder builder) {
        filterParameters(RequestParam.class, (requestParam, obj) -> {
            if (obj != null) {
                builder.queryParam(getRequestParameterName(requestParam), obj);
            }
        });
        return builder;
    }

    private UriComponents bindPathVariables(UriComponentsBuilder builder) {
        HashMap<String, Object> variables = new HashMap<>();
        filterParameters(PathVariable.class, (param, obj) -> {
            String pathVariable = getPathVariableName(param);
            if (obj == null) {
                obj = PATHVARSTART + pathVariable + PATHVAREND;
            }
            variables.put(pathVariable, obj);
        });
        return builder.buildAndExpand(variables);
    }

    private String getPathVariableName(Parameter parameter) {
        String value = parameter.getAnnotation(PathVariable.class).value();
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("no value for PathVariable given");
        }
        return value;
    }

    private String getRequestParameterName(Parameter parameter) {
        String value = parameter.getAnnotation(RequestParam.class).value();
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("no value for RequestParam given");
        }
        return value;
    }

    private <T extends Annotation> void filterParameters(Class<T> filterType, BiConsumer<Parameter, Object> handler) {
        Parameter[] parameters = invocation.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getAnnotation(filterType) != null) {
                handler.accept(parameter, invocation.getArguments()[i]);
            }
        }
    }

    public Link withGivenRel() {
        Relation annotation = invocation.getMethod().getAnnotation(Relation.class);
        if (annotation == null) {
            throw new IllegalArgumentException("expected method '" + getMethodDescription() + "' to be annotated with " + Relation.class.getName());
        }
        return withRel(annotation.value());
    }

    private String getMethodDescription() {
        return invocation.getMethod().getDeclaringClass().getName() +
                "." + invocation.getMethod().getName() + "()";
    }
}

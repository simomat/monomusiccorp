package de.infonautika.monomusiccorp.app.controller.utils.links;

import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.BiConsumer;

public class RelationMethod {
    private Method method;

    public RelationMethod(Method method) {
        this.method = method;
    }

    public String getRelation() {
        return method.getAnnotation(Relation.class).value();
    }

    public Collection<RequestMethod> getRequestMethods() {
        return Optional.ofNullable(method.getAnnotation(RequestMapping.class))
                .map(RequestMapping::method)
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
    }

    public Map<String, Class<?>> getPathVariables() {
        Map<String, Class<?>> pathVars = new HashMap<>();
        typeParametersOfAnnotation(
                PathVariable.class,
                (pathVariable, clazz) -> pathVars.put(pathVariable.value(), clazz));
        return pathVars;
    }

    public Map<String, Class<?>> getRequestParameters() {
        Map<String, Class<?>> requestParams = new HashMap<>();
        typeParametersOfAnnotation(
                RequestParam.class,
                (requestParam, clazz) -> requestParams.put(requestParam.value(), clazz));
        return requestParams;
    }

    private <T extends Annotation> void typeParametersOfAnnotation(Class<T> annotationClass, BiConsumer<T, Class<?>> matchHandler) {
        for (Parameter parameter: method.getParameters()) {
            T annotation = parameter.getAnnotation(annotationClass);
            if (annotation != null) {
                matchHandler.accept(annotation, parameter.getType());
            }
        }
    }

    public Class<?> getRequestBody() {
        for (Parameter parameter: method.getParameters()) {
            if (parameter.getAnnotation(RequestBody.class) != null) {
                return parameter.getType();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationMethod that = (RelationMethod) o;
        return Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method);
    }
}


package de.infonautika.monomusiccorp.app.controller.utils.links;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelationMethodRegistry {

    private final Logger logger = LoggerFactory.getLogger(RelationMethodRegistry.class);

    private final JsonSchemaGenerator schemaGen;
    private Map<String, RelationMethod> methods = new HashMap<>();

    public RelationMethodRegistry() {
        ObjectMapper mapper = new ObjectMapper();
        schemaGen = new JsonSchemaGenerator(mapper);
    }

    public void register(Method method) {
        Assert.notNull(method.getAnnotation(Relation.class));
        RelationMethod relMethod = new RelationMethod(method);
        String relation = relMethod.getRelation();
        if (methods.containsKey(relation)) {
            throw new RuntimeException("method with relation '" + relation + "' already registered");
        }
        methods.put(relation, relMethod);
    }

    public Optional<Object> getCuri(String relationName) {
        return Optional.ofNullable(methods.get(relationName))
                .map(this::toCuriInfo);
    }

    private Object toCuriInfo(RelationMethod relationMethod) {
        return new CuriInfo(
                relationMethod.getRequestMethods(),
                toSchemaMap(relationMethod.getPathVariables()),
                toSchemaMap(relationMethod.getRequestParameters()),
                getRequestBodySchema(relationMethod));
    }

    private JsonSchema getRequestBodySchema(RelationMethod relationMethod) {
        Class<?> requestBody = relationMethod.getRequestBody();
        if (requestBody != null) {
            try {
                return toSchema(requestBody);
            } catch (JsonMappingException e) {
                logger.warn("could not map class '" + requestBody.getName() + "' to json schema");
            }
        }
        return null;
    }

    private Map<String, JsonSchema> toSchemaMap(Map<String, Class<?>> classMap) {
        HashMap<String, JsonSchema> mapped = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry: classMap.entrySet()){
            try {
                mapped.put(entry.getKey(), toSchema(entry.getValue()));
            } catch (JsonMappingException e) {
                logger.warn("could not map class '" + entry.getValue().getName() + "' to json schema");
            }
        }
        return mapped;
    }

    private JsonSchema toSchema(Class<?> clazz) throws JsonMappingException {
        return schemaGen.generateSchema(clazz);
    }

    private class CuriInfo {
        private final Collection<RequestMethod> methods;
        private final Map<String, JsonSchema> pathVariables;
        private final Map<String, JsonSchema> requestParameters;
        private final JsonSchema bodySchema;

        public CuriInfo(Collection<RequestMethod> methods, Map<String, JsonSchema> pathVariables, Map<String, JsonSchema> requestParameters, JsonSchema bodySchema) {
            this.methods = methods;
            this.pathVariables = pathVariables;
            this.requestParameters = requestParameters;
            this.bodySchema = bodySchema;
        }

        public Collection<RequestMethod> getMethods() {
            return methods;
        }

        public Map<String, JsonSchema> getPathVariables() {
            return pathVariables;
        }

        public Map<String, JsonSchema> getRequestParameters() {
            return requestParameters;
        }

        public JsonSchema getBodySchema() {
            return bodySchema;
        }
    }
}

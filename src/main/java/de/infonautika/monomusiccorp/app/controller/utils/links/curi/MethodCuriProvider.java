package de.infonautika.monomusiccorp.app.controller.utils.links.curi;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class MethodCuriProvider {

    private static final Logger logger = LoggerFactory.getLogger(MethodCuriProvider.class);

    @Autowired
    private JsonSchemaGenerator schemaGen;

    public CuriInfo toCuriInfo(RelationMethod relationMethod) {
        return new CuriInfo(
                relationMethod.getUri(),
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
}

package de.infonautika.monomusiccorp.app.controller.utils.links.curi;

import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelationMethodRegistry {

    private Map<String, RelationMethod> methods = new HashMap<>();

    public void register(Method method) {
        Assert.notNull(method.getAnnotation(Relation.class));
        RelationMethod relMethod = new RelationMethod(method);
        String relation = relMethod.getRelation();
        if (methods.containsKey(relation)) {
            throw new RuntimeException("method with relation '" + relation + "' already registered");
        }
        methods.put(relation, relMethod);
    }

    public Optional<RelationMethod> getCuri(String relationName) {
        return Optional.ofNullable(methods.get(relationName));
    }

}

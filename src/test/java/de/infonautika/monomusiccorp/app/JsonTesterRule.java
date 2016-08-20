package de.infonautika.monomusiccorp.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.rules.ExternalResource;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

public class JsonTesterRule<T> extends ExternalResource {

    private JacksonTester<T> jsonTester;

    @Override
    protected void before() throws Throwable {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    public JsonContent<T> write(T item) throws IOException {
        return jsonTester.write(item);
    }
}

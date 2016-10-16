package de.infonautika.monomusiccorp.app.controller.utils.links;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static de.infonautika.monomusiccorp.app.controller.utils.links.InvocationProxy.methodOn;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LinkCreatorTest {

    @Test
    public void noParametersBaseMapping() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).noParametersNoMapping());

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base"));
    }

    @Test
    public void noParametersOwnMapping() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).noParameters());

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub"));
    }

    @Test
    public void expandRequestParametersByAnnotationValue() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).requestParams("123"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub?id=123"));
    }


    @Test
    public void manyRequestParameters() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).manyRequestParams("123", "456"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub?id=123&other=456"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requestParametersWithoutValueThrows() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).requestParameterWithoutValueIsInvalid("123"));

        new LinkCreator(invocation).getHref();
    }

    @Test
    public void requestParameterIsEncoded() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).requestParams(new Object(){
            @Override
            public String toString() {
                return "/\\{#";
            }
        }));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub?id=/%5C%7B%23"));
    }

    @Test
    public void notAnnotatedParameterAreIgnored() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).noAnnotation(null, "123"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub"));
    }

    @Test
    public void nullRequestParameterIsIgnored() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).manyRequestParams("123", null));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/sub?id=123"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathVariableWithoutValueThrows() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).pathVariableWithoutValueIsInvalid("123"));

        new LinkCreator(invocation).getHref();
    }

    @Test
    public void pathVariableExpands() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).pathVariable("123"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/123/other"));
    }

    @Test
    public void pathVariableIsEncoded() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).pathVariable(new Object(){
            @Override
            public String toString() {
                return "/\\{#";
            }
        }));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base//%5C%7B%23/other"));
    }

    @Test
    public void manyPathVariableRandomOrder() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).manyPathVariables("123", "456"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/456/other/123"));
    }

    @Test
    public void nullPathVariableCreatesTemplate() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).pathVariable(null));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/{var}/other"));
    }

    @Test
    public void regularAndTemplatePathVariableCreatesTemplate() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).manyPathVariables("123", null));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/{first}/other/123"));
    }

    @Test
    public void pathVariableAndRequestParameterExpand() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).combined("123", "456"));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/456/other?param=123"));
    }

    @Test
    public void templatePathVariableAndRequestParameterExpand() throws Exception {
        Invocation invocation = invocationOf(methodOn(MyController.class).combined("123", null));

        LinkCreator linkCreator = new LinkCreator(invocation);

        assertThat(linkCreator.getHref(), is("/base/{var}/other?param=123"));
    }

    @Test
    public void withGivenRelFailsOnUnannotatedMethod() throws Exception {
        LinkCreator linkCreator = new LinkCreator(invocationOf(methodOn(MyController.class).noParametersNoMapping()));
        linkCreator.withGivenRel();

    }

    private Invocation invocationOf(Object invocation) {
        return (Invocation) invocation;
    }



    @RequestMapping("/base")
    public class MyController {

        @RequestMapping
        public ResponseEntity noParametersNoMapping() {
            return null;
        }

        @RequestMapping("sub")
        public ResponseEntity noParameters() {
            return null;
        }

        @RequestMapping("sub")
        public ResponseEntity requestParams(@RequestParam("id") Object noid) {
            return null;
        }

        @RequestMapping("sub")
        public Object manyRequestParams(@RequestParam("id") String s, @RequestParam("other") String noid) {
            return null;
        }

        @RequestMapping("sub")
        public Object requestParameterWithoutValueIsInvalid(@RequestParam String s) {
            return null;
        }

        @RequestMapping("sub")
        public Object noAnnotation(String s, Object o) {
            return null;
        }

        @RequestMapping("{var}")
        public Object pathVariableWithoutValueIsInvalid(@PathVariable String s) {
            return null;
        }

        @RequestMapping("{var}/other")
        public Object pathVariable(@PathVariable("var") Object s) {
            return null;
        }

        @RequestMapping("{first}/other/{second}")
        public Object manyPathVariables(@PathVariable("second") String s, @PathVariable("first") String s1) {
            return null;
        }

        @RequestMapping("{var}/other")
        public Object combined(@RequestParam("param") String s, @PathVariable("var") String s1) {
            return null;
        }
    }
}
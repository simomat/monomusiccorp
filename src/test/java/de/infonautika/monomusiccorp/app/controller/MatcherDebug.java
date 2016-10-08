package de.infonautika.monomusiccorp.app.controller;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

public class MatcherDebug {
    public static ResultMatcher debug(ResultMatcher matcher) {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult result) throws Exception {
                MockHttpServletResponse response = result.getResponse();
                print("status: " + response.getStatus());
                print("content: " + response.getContentAsString());
                matcher.match(result);
            }

            private void print(String string) {
                System.out.println(string);
            }
        };
    }
}

package de.infonautika.monomusiccorp.app.controller;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;

public class MatcherDebug {

    public static ResultMatcher debug(ResultMatcher matcher) {
        return result -> {
            output(result);
            matcher.match(result);
        };
    }

    public static ResultMatcher debug() {
        return result -> output(result);
    }

    private static void print(String string) {
        System.out.println(string);
    }

    private static void output(MvcResult result) throws UnsupportedEncodingException {
        MockHttpServletResponse response = result.getResponse();
        print("status: " + response.getStatus());
        print("content: " + response.getContentAsString());
    }
}

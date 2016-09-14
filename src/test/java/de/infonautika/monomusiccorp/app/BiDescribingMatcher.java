package de.infonautika.monomusiccorp.app;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class BiDescribingMatcher<E, A> extends TypeSafeMatcher<A> {
    private final E expected;
    private final BiPredicate<A, E> matcher;
    private final Function<E, String> expectedDescriber;
    private final Function<A, String> actualDescriber;

    public BiDescribingMatcher(E expected, BiPredicate<A, E> matcher, Function<E, String> expectedDescriber, Function<A, String> actualDescriber) {
        this.expected = expected;
        this.matcher = matcher;
        this.expectedDescriber = expectedDescriber;
        this.actualDescriber = actualDescriber;
    }

    @Override
    protected boolean matchesSafely(A actual) {
        return matcher.test(actual, expected);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expectedDescriber.apply(expected));
    }

    @Override
    protected void describeMismatchSafely(A actual, Description mismatchDescription) {
        mismatchDescription.appendText(actualDescriber.apply(actual));
    }
}

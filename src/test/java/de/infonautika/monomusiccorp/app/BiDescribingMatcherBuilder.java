package de.infonautika.monomusiccorp.app;

import org.hamcrest.Matcher;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class BiDescribingMatcherBuilder<E, A> {
    private E expected;
    private BiPredicate<A, E> matcher;
    private Function<E, String> expectedDescriber;
    private Function<A, String> actualDescriber;

    public BiDescribingMatcherBuilder(E expected) {
        this.expected = expected;
    }

    public static <E, A> BiDescribingMatcherBuilder<E, A> matcherForExpected(E expected) {
        return new BiDescribingMatcherBuilder<>(expected);
    }

    public BiDescribingMatcherBuilder<E, A> withMatcher(BiPredicate<A, E> matcher) {
        this.matcher = matcher;
        return this;
    }

    public BiDescribingMatcherBuilder<E, A> withExpectedDescriber(Function<E, String> expectedDescriber) {
        this.expectedDescriber = expectedDescriber;
        return this;
    }

    public BiDescribingMatcherBuilder<E, A> withActualDescriber(Function<A, String> actualDescriber) {
        this.actualDescriber = actualDescriber;
        return this;
    }

    public Matcher<A> andBuild() {
        return new BiDescribingMatcher<>(
                expected,
                matcher,
                expectedDescriber,
                actualDescriber);
    }
}

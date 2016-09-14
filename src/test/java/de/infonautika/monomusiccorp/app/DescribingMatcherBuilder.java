package de.infonautika.monomusiccorp.app;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class DescribingMatcherBuilder<T> {
    private T expected;
    private BiPredicate<T, T> matcher;
    private Function<T, String> describer;

    protected DescribingMatcherBuilder(T expected) {
        this.expected = expected;
    }

    public DescribingMatcherBuilder<T> matchesWith(BiPredicate<T, T> matcher) {
        this.matcher = matcher;
        return this;
    }

    public DescribingMatcherBuilder<T> describesTo(Function<T, String> describer) {
        this.describer = describer;
        return this;
    }

    public BiDescribingMatcher<T, T> andBuild() {
        Objects.requireNonNull(expected);
        Objects.requireNonNull(matcher);
        Objects.requireNonNull(describer);
        return new BiDescribingMatcher<>(expected, matcher, describer, describer);
    }

    public static <T> DescribingMatcherBuilder<T> matcherForExpected(T expected) {
        return new DescribingMatcherBuilder<>(expected);
    }

}

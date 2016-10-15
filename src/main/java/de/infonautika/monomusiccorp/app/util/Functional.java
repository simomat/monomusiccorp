package de.infonautika.monomusiccorp.app.util;

import java.util.Optional;
import java.util.function.Consumer;

public class Functional {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> Optional<T> ifPresent(Optional<T> optional, Consumer<T> consumer) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        return optional;
    }
}

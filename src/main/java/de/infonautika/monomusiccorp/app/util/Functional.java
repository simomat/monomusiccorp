package de.infonautika.monomusiccorp.app.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Functional {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> VoidOptional<T> ifPresent(Optional<T> optional, Consumer<T> consumer) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        }
        return VoidOptional.ofNullable(optional);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class VoidOptional<T> {
        private Optional<T> optional;

        public VoidOptional(Optional<T> optional) {
            this.optional = optional;
        }

        public static <T> VoidOptional<T> ofNullable(Optional<T> optional) {
            return new VoidOptional<>(optional);
        }

        public void orElseDo(Runnable runnable) {
            if (!optional.isPresent()) {
                runnable.run();
            }
        }

        public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            optional.orElseThrow(exceptionSupplier);
        }
    }
}

package de.infonautika.monomusiccorp.app.util;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {
    public static <T> Stream<T> stream(Iterator<T> sourceIterator) {
        return stream(sourceIterator, false);
    }

    public static <T> Stream<T> stream(Iterator<T> sourceIterator, boolean parallel) {
        return StreamSupport.stream(((Iterable<T>) () -> sourceIterator).spliterator(), parallel);
    }
}

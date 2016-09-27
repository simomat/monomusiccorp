package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Product;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface ProductLookup {
    Collection<Product> findAll();

    Optional<Product> findOne(String productId);

    boolean exists(String productId);

    <T> T withProducts(Stream<String> productIds, Function<Stream<Product>, T> consumer, Supplier<T> orElse);
}

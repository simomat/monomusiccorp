package de.infonautika.monomusiccorp.app.repository;

import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class ProductLookupImpl implements ProductLookup {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Collection<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findOne(String productId) {
        return Optional.ofNullable(productRepository.findOne(productId));
    }

    @Override
    public boolean exists(String productId) {
        return productRepository.exists(productId);
    }

    @Override
    public <T> T withProducts(Stream<String> productIds, Function<Stream<Product>, T> consumer, Supplier<T> orElse) {
        List<String> ids = productIds.collect(toList());
        if (!ids.isEmpty()) {
            try (Stream<Product> products = productRepository.findByIdIn(ids)) {
                if (products != null) {
                    return consumer.apply(products);
                }
            }
        }
        return orElse.get();
    }


}

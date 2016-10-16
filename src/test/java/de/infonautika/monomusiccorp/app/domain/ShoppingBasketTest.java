package de.infonautika.monomusiccorp.app.domain;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ShoppingBasketTest {

    private ShoppingBasket shoppingBasket;

    @Before
    public void setUp() throws Exception {
        shoppingBasket = new ShoppingBasket();
    }

    @Test
    public void putOneIsInBasket() throws Exception {
        Product product = productOf("2");

        shoppingBasket.put(product, 1L);

        assertThat(shoppingBasket.getPositions(), contains(new Position(product, 1L)));
    }

    private Product productOf(String productId) {
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    @Test
    public void putOneAndAddMoreWithSameId() throws Exception {
        Product product = productOf("2");

        shoppingBasket.put(product, 1L);
        shoppingBasket.put(product, 5L);

        assertThat(shoppingBasket.getPositions(), contains(new Position(product, 6L)));
    }

    @Test
    public void putManyRemoveSomeWithSameId() throws Exception {
        Product product = productOf("2");

        shoppingBasket.put(product, 10L);
        shoppingBasket.remove("2", 6L);

        assertThat(shoppingBasket.getPositions(), contains(new Position(product, 4L)));
    }


    @Test
    public void doesNotAddZeroOrNegativeQuantities() throws Exception {
        Product product1 = productOf("1");
        Product product2 = productOf("2");

        shoppingBasket.put(product1, -1L);
        shoppingBasket.put(product2, 0L);

        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(product1))));
        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(product2))));
    }

    private static TypeSafeMatcher<Position> positionWithId(Product product) {
        return new TypeSafeMatcher<Position>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Position with " + product);
            }

            @Override
            protected boolean matchesSafely(Position item) {
                return item.getProduct().getId().equals(product.getId());
            }
        };
    }

    @Test
    public void removeAllRemovesPosition() throws Exception {
        Product product = productOf("2");

        shoppingBasket.put(product, 5L);
        shoppingBasket.remove(product, 5L);

        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(product))));
    }

    @Test
    public void noDuplicates() throws Exception {
        Product product = productOf("2");

        shoppingBasket.put(product, 8L);
        shoppingBasket.put(product, 1L);

        assertThat(shoppingBasket.getPositions(), hasSize(1));
    }

    @Test
    public void rightItemWasUpdated() throws Exception {
        Product product1 = productOf("1");
        Product product2 = productOf("2");

        shoppingBasket.put(product1, 1L);
        shoppingBasket.put(product2, 1L);

        assertThat(shoppingBasket.getPositions(), containsInAnyOrder(new Position(product1, 1L), new Position(product2, 1L)));
    }

}
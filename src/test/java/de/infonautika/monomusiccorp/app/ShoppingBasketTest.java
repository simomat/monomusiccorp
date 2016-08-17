package de.infonautika.monomusiccorp.app;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import static de.infonautika.monomusiccorp.app.ItemIdCreator.createItemId;
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
        ItemId itemId = createItemId();

        shoppingBasket.put(itemId, 1);

        assertThat(shoppingBasket.getPositions(), contains(new Position(itemId, 1)));
    }

    @Test
    public void putOneAndAddMoreWithSameId() throws Exception {
        ItemId itemId = createItemId();

        shoppingBasket.put(itemId, 1);
        shoppingBasket.put(itemId, 5);

        assertThat(shoppingBasket.getPositions(), contains(new Position(itemId, 6)));
    }

    @Test
    public void putManyRemoveSomeWithSameId() throws Exception {
        ItemId itemId = createItemId();

        shoppingBasket.put(itemId, 10);
        shoppingBasket.remove(itemId, 6);

        assertThat(shoppingBasket.getPositions(), contains(new Position(itemId, 4)));
    }


    @Test
    public void doesNotAddZeroOrNegativeQuantities() throws Exception {
        ItemId itemIdOne = createItemId();
        ItemId itemIdTwo = createItemId();

        shoppingBasket.put(itemIdOne, -1);
        shoppingBasket.put(itemIdTwo, 0);

        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(itemIdOne))));
        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(itemIdTwo))));
    }

    private static TypeSafeMatcher<Position> positionWithId(ItemId itemId) {
        return new TypeSafeMatcher<Position>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Position with ItemId " + itemId);
            }

            @Override
            protected boolean matchesSafely(Position item) {
                return item.getItemId().equals(itemId);
            }
        };
    }

    @Test
    public void removeAllRemovesPosition() throws Exception {
        ItemId itemId = createItemId();

        shoppingBasket.put(itemId, 5);
        shoppingBasket.remove(itemId, 5);

        assertThat(shoppingBasket.getPositions(), not(contains(positionWithId(itemId))));
    }

    @Test
    public void noDuplicates() throws Exception {
        ItemId itemId = createItemId();

        shoppingBasket.put(itemId, 8);
        shoppingBasket.put(itemId, 1);

        assertThat(shoppingBasket.getPositions(), hasSize(1));
    }

    @Test
    public void rightItemWasUpdated() throws Exception {
        ItemId itemIdOne = createItemId();
        ItemId itemIdTwo = createItemId();

        shoppingBasket.put(itemIdOne, 1);
        shoppingBasket.put(itemIdTwo, 1);

        assertThat(shoppingBasket.getPositions(), containsInAnyOrder(new Position(itemIdOne, 1), new Position(itemIdTwo, 1)));
    }


}
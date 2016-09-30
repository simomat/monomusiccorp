package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.BiDescribingMatcherBuilder;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.infonautika.monomusiccorp.app.DescribingMatcherBuilder.matcherForExpected;
import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.internal.matchers.Equality.areEqual;

@RunWith(MockitoJUnitRunner.class)
public class BusinessProcessImplTest {

    private CustomerInfo hans = new CustomerInfo("hans", "hans", "Hechtstr. 21");

    @InjectMocks
    public BusinessProcessImpl businessProcess;

    @Mock
    private ProductLookup productLookup;

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingBasketRepository shoppingBasketRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private CustomerLookup customerLookup;

    @Mock
    private PickingOrderRepository pickingOrderRepository;

    @Mock
    private StockNotification stockNotification;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceDelivery invoiceDelivery;

    @Captor
    private ArgumentCaptor<PickingOrder> pickingOrderCaptor;
    @Captor
    private ArgumentCaptor<ShoppingBasket> shoppingBasketCaptor;
    @Captor
    private ArgumentCaptor<StockItem> stockItemCaptor;
    @Captor
    private ArgumentCaptor<Invoice> invoiceCaptor;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Test
    public void addCustomerWithExistingNameFails() throws Exception {
        when(securityService.addUser(any())).thenReturn(ResultStatus.USER_EXISTS);

        ResultStatus status = businessProcess.addCustomer(hans);

        assertThat(status, is(ResultStatus.USER_EXISTS));
    }

    @Test
    public void addCustomerReturnsOk() throws Exception {
        when(securityService.addUser(any())).thenReturn(ResultStatus.OK);

        ResultStatus status = businessProcess.addCustomer(hans);

        assertThat(status, is(ResultStatus.OK));
    }

    @Test
    public void addCustomerCreatesCustomer() throws Exception {
        when(securityService.addUser(any())).thenReturn(ResultStatus.OK);

        businessProcess.addCustomer(hans);

        verify(customerLookup).save(argThat(customer -> customer.getUsername().equals("hans")));
    }

    @Test
    public void addStockItemWithoutProductFails() throws Exception {
        stateSetup()
                .havingProducts();

        ResultStatus actual = businessProcess.addItemToStock(Quantity.of(new ItemId("10"), 5L));

        assertThat(actual, is(ResultStatus.NOT_EXISTENT));
    }

    @Test
    public void addStockItemCreatesNew() throws Exception {
        ItemId itemId = ItemId.of("10");
        Product product = productOf(itemId);

        when(productLookup.findOne(itemId.getId())).thenReturn(Optional.of(product));

        businessProcess.addItemToStock(Quantity.of(itemId, 5L));

        verify(stockItemRepository).save(stockItemCaptor.capture());
        assertThat(stockItemCaptor.getValue(), equalsStockItem(StockItem.of(product, 5L)));
    }

    @Test
    public void addStockItemUpdatesExisting() throws Exception {
        stateSetup()
                .havingStockOf(StockItem.of(productOf(ItemId.of("10")), 5L));

        businessProcess.addItemToStock(Quantity.of(ItemId.of("10"), 6L));

        verify(stockItemRepository).save(stockItemCaptor.capture());
        assertThat(stockItemCaptor.getValue(), equalsStockItem(StockItem.of(productOf(ItemId.of("10")), 11L)));
    }

    private Matcher<StockItem> equalsStockItem(StockItem stockItem) {
        return matcherForExpected(stockItem)
                .matchesWith((actual, expected) ->
                        areEqual(expected.getQuantity(), actual.getQuantity()) &&
                        areEqual(expected.getProduct().getItemId(), actual.getProduct().getItemId()))
                .describesTo((item) -> item.getClass().getSimpleName() + " with quantity " + item.getQuantity() +
                        " and Product " + item.getProduct().getItemId())
                .andBuild();
    }

    @Test
    public void putNonExistingItemToBasketFails() throws Exception {
        ResultStatus actual = businessProcess.putToBasket("", Quantity.of(ItemId.of("5"), 3L));

        assertThat(actual, is(ResultStatus.NOT_EXISTENT));
    }

    @Test
    public void putToBasketReturnsOk() throws Exception {
        stateSetup()
                .productExistsReturnsTrue()
                .emptyShoppingBasket();

        ResultStatus actual = businessProcess.putToBasket("", Quantity.of(new ItemId("5"), 3L));

        assertThat(actual, is(ResultStatus.OK));
    }

    @Test
    public void putToBasketUpdatesBasket() throws Exception {
        stateSetup()
                .productExistsReturnsTrue()
                .emptyShoppingBasket();

        businessProcess.putToBasket("", Quantity.of(ItemId.of("4"), 3L));

        // when verifying with argThat(Matcher<T> matcher),
        // Mockito does not call TypeSafeMatcher.describeMismatchSafely()
        // so a mismatching accepted value is only described with toString() :(
//        verify(shoppingBasketRepository).save(argThat(containsPosition(Position.of(ItemId.of("5"), 3L))));

        verify(shoppingBasketRepository).save(shoppingBasketCaptor.capture());
        assertThat(shoppingBasketCaptor.getValue(), containsPosition(Position.of(ItemId.of("4"), 3L)));
    }

    private Matcher<HasPositions> containsPosition(Position position) {
        return BiDescribingMatcherBuilder
                .<Position, HasPositions>matcherForExpected(position)
                    .withMatcher((actual, expected) -> actual.getPositions().contains(expected))
                    .withExpectedDescriber((expected) -> expected.getClass().getSimpleName() + " containing " + expected)
                    .withActualDescriber((actual) -> actual.getClass().getSimpleName() + " containsPosition [" +
                            actual.getPositions().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(", ")) + "]")
                    .andBuild();
    }

    private Matcher<HasPricedPositions> containsPricedPosition(PricedPosition position) {
        return BiDescribingMatcherBuilder
                .<PricedPosition, HasPricedPositions>matcherForExpected(position)
                .withMatcher((actual, expected) -> actual.getPositions().contains(expected))
                .withExpectedDescriber((expected) -> expected.getClass().getSimpleName() + " containing " + expected)
                .withActualDescriber((actual) -> actual.getClass().getSimpleName() + " containsPricedPosition [" +
                        actual.getPositions().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")) + "]")
                .andBuild();
    }

    @Test
    public void contentOfEmptyBasketIsEmpty() throws Exception {
        stateSetup()
                .emptyShoppingBasket();

        List<PricedPosition> basketContent = businessProcess.getBasketContent("");

        assertThat(basketContent, is(empty()));
    }

    @Test
    public void contentOfBasketIsReturned() throws Exception {
        ItemId itemId1 = ItemId.of("1");
        ItemId itemId2 = ItemId.of("2");

        stateSetup()
                .havingProducts(
                        productOf(itemId1),
                        productOf(itemId2),
                        productOf(ItemId.of("3")))
                .havingShoppingBasket(
                        Position.of(itemId1, 1L),
                        Position.of(itemId2, 2L));


        List<PricedPosition> basketContent = businessProcess.getBasketContent("");

        //noinspection unchecked
        assertThat(basketContent, containsInAnyOrder(
                quantityOfProduct(itemId1, 1L),
                quantityOfProduct(itemId2, 2L)));
    }

    @Test
    public void removeFromBasketUpdatesBasket() throws Exception {
        Position position = Position.of(ItemId.of("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.removeFromBasket("", Quantity.of(ItemId.of("5"), 3L));

        verify(shoppingBasketRepository).save(shoppingBasketCaptor.capture());
        assertThat(shoppingBasketCaptor.getValue(), containsPosition(Position.of(ItemId.of("5"), 1L)));
    }

    @Test
    public void submitOrderWithEmptyBasketFails() throws Exception {
        stateSetup()
                .emptyShoppingBasket();

        ResultStatus status = businessProcess.submitOrder("3");

        assertThat(status, is(ResultStatus.NOT_EXISTENT));
    }

    @Test
    public void submitOrderCreatesOrderWithPositions() throws Exception {
        ItemId itemId = ItemId.of("5");
        Money price = Money.of(12d, EUR);
        stateSetup()
                .havingProducts(productOf(itemId, price))
                .havingShoppingBasket(Position.of(itemId, 4L));

        ResultStatus status = businessProcess.submitOrder("3");

        assertThat(status, is(ResultStatus.OK));

        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue(), containsPricedPosition(PricedPosition.of(itemId, 4L, price)));

    }

    private Product productOf(ItemId itemId, Money price) {
        Product product = productOf(itemId);
        product.setPrice(price);
        return product;
    }

    @Test
    public void submitOrderCreatesPickingOrder() throws Exception {
        Position position = Position.of(ItemId.of("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(pickingOrderRepository).save(pickingOrderCaptor.capture());
        assertThat(pickingOrderCaptor.getValue(), not(containsPosition(position)));
        assertThat(pickingOrderCaptor.getValue().getStatus(), is(PickingOrder.PickingStatus.OPEN));
    }

    @Test
    public void submitOrderNotifiesStock() throws Exception {
        Position position = Position.of(ItemId.of("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(stockNotification).newPickingOrder(pickingOrderCaptor.capture());
        assertThat(pickingOrderCaptor.getValue(), not(containsPosition(position)));
        assertThat(pickingOrderCaptor.getValue().getStatus(), is(PickingOrder.PickingStatus.OPEN));
    }

    @Test
    public void submitOrderClearsShoppingBasket() throws Exception {
        Position position = Position.of(ItemId.of("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(customerLookup).save(argThat(customer -> customer.getShoppingBasket().isEmpty()));
    }

    @Test
    public void submitOrderCreatesInvoice() throws Exception {
        ItemId itemId = ItemId.of("5");
        Money price = Money.of(2d, EUR);
        stateSetup()
                .havingProducts(productOf(itemId, price))
                .havingShoppingBasket(Position.of(itemId, 4L));

        businessProcess.submitOrder("3");

        verify(invoiceRepository).save(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue(), containsPricedPosition(PricedPosition.of(itemId, 4L, price)));
    }

    @Test
    public void submitOrderDeliversInvoice() throws Exception {
        ItemId itemId = ItemId.of("5");
        Money price = Money.of(2d, EUR);
        stateSetup()
                .havingProducts(productOf(itemId, price))
                .havingShoppingBasket(Position.of(itemId, 4L));

        businessProcess.submitOrder("3");

        verify(invoiceDelivery).deliver(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue(), containsPricedPosition(PricedPosition.of(itemId, 4L, price)));
    }

    private StateSetup stateSetup() {
        return new StateSetup();
    }

    private Product productOf(ItemId itemId) {
        Product product = new Product();
        product.setItemId(itemId);
        return product;
    }

    private Quantity<Product> quantityOfProduct(ItemId itemId, long quantity) {
        Product product = new Product();
        product.setItemId(itemId);
        return Quantity.of(product, quantity);
    }

    private class StateSetup {
        private Customer customer = new Customer();

        @SuppressWarnings("unchecked")
        public StateSetup havingProducts(Product... products) {
            doAnswer(invocation -> {
                String id = invocation.getArgument(0);
                return stream(products)
                        .filter(p -> p.getItemId().getId().equals(id))
                        .findFirst();
            }).when(productLookup).findOne(anyString());

            doAnswer(invocation -> {
                Function<Stream<Product>, ?> streamFunction = invocation.getArgument(1);
                return streamFunction.apply(
                        join(stream(products))
                        .withKey(p -> p.getItemId().getId())
                        .on((Stream<String>) invocation.getArgument(0))
                        .withKey(Function.identity())
                        .group((p, idStream) -> p)
                        .asStream());
            }).when(productLookup).withProducts(any(), any(), any());

            return this;
        }

        public StateSetup havingShoppingBasket(Position... positions) {
            Customer customer = getCustomer();
            customer.setShoppingBasket(shoppingBasketWith(asList(positions)));
            when(customerLookup.getCustomer(anyString()))
                    .thenReturn(Optional.of(customer));
            return this;
        }

        public StateSetup productExistsReturnsTrue() {
            when(productLookup.exists(anyString())).thenReturn(true);
            return this;
        }

        public StateSetup emptyShoppingBasket() {
            Customer customer = getCustomer();
            customer.setShoppingBasket(new ShoppingBasket());
            when(customerLookup.getCustomer(anyString()))
                    .thenReturn(Optional.of(customer));
            return this;
        }

        public StateSetup havingStockOf(StockItem... stockItems) {
            doAnswer(invocation -> {
                String id = (String) invocation.getArguments()[0];
                //noinspection OptionalGetWithoutIsPresent
                return stream(stockItems)
                        .filter(stockItem -> stockItem.getProduct().getItemId().getId().equals(id))
                        .findFirst().get();

            }).when(stockItemRepository).findByProductId(anyString());

            return this;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    private ShoppingBasket shoppingBasketWith(List<Position> positions) {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        positions.forEach(position -> shoppingBasket.put(position.getItemId(), position.getQuantity()));
        return shoppingBasket;
    }
}
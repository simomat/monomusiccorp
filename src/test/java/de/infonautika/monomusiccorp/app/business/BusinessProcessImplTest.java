package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.BiDescribingMatcherBuilder;
import de.infonautika.monomusiccorp.app.business.errors.ConflictException;
import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
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
import java.util.function.Consumer;
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

    @Test(expected = ConflictException.class)
    public void addCustomerWithExistingNameFails() throws Exception {
        doThrow(ConflictException.class).when(securityService).addUser(any());

        businessProcess.addCustomer(hans);
    }

    @Test
    public void addCustomerSavesUserAndCustomer() throws Exception {
        businessProcess.addCustomer(hans);

        verify(securityService).addUser(hans);
        verify(customerLookup).save(argThat(customer -> customer.getUsername().equals("hans")));
    }

    // TODO: 11.10.16 add test for atomicity

    @Test
    public void addStockItemWithoutProductFails() throws Exception {
        stateSetup()
                .havingProducts();

        ResultStatus actual = businessProcess.addItemToStock(Quantity.of("10", 5L));

        assertThat(actual, is(ResultStatus.NOT_EXISTENT));
    }

    @Test
    public void addStockItemCreatesNew() throws Exception {
        String itemId = "10";
        Product product = productOf(itemId);

        when(productLookup.findOne(itemId)).thenReturn(Optional.of(product));

        businessProcess.addItemToStock(Quantity.of(itemId, 5L));

        verify(stockItemRepository).save(stockItemCaptor.capture());
        assertThat(stockItemCaptor.getValue(), equalsStockItem(StockItem.of(product, 5L)));
    }

    @Test
    public void addStockItemUpdatesExisting() throws Exception {
        stateSetup()
                .havingStockOf(StockItem.of(productOf("10"), 5L));

        businessProcess.addItemToStock(Quantity.of("10", 6L));

        verify(stockItemRepository).save(stockItemCaptor.capture());
        assertThat(stockItemCaptor.getValue(), equalsStockItem(StockItem.of(productOf("10"), 11L)));
    }

    private Matcher<StockItem> equalsStockItem(StockItem stockItem) {
        return matcherForExpected(stockItem)
                .matchesWith((actual, expected) ->
                        areEqual(expected.getQuantity(), actual.getQuantity()) &&
                        areEqual(expected.getProduct().getId(), actual.getProduct().getId()))
                .describesTo((item) -> item.getClass().getSimpleName() + " with quantity " + item.getQuantity() +
                        " and Product " + item.getProduct().getId())
                .andBuild();
    }

    @Test(expected = DoesNotExistException.class)
    public void putNonExistingItemToBasketFails() throws Exception {
        businessProcess.putToBasket("", "5", 3L);
    }

    @Test
    public void putToBasketUpdatesBasket() throws Exception {
        Product product = productOf("4");
        stateSetup()
                .havingProducts(product)
                .emptyShoppingBasket();

        businessProcess.putToBasket("", "4", 3L);

        verify(shoppingBasketRepository).save(shoppingBasketCaptor.capture());
        assertThat(shoppingBasketCaptor.getValue(), containsPosition(Position.of(product, 3L)));
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

        List<Position> basketContent = businessProcess.getBasketContent("");

        assertThat(basketContent, is(empty()));
    }

    @Test
    public void contentOfBasketIsReturned() throws Exception {
        String productId1 = "1";
        String productId2 = "2";

        Product product1 = productOf(productId1, Money.of(1d, EUR));
        Product product2 = productOf(productId2, Money.of(2d, EUR));
        stateSetup()
                .havingProducts(
                        product1,
                        product2,
                        productOf("3"))
                .havingShoppingBasket(
                        Position.of(product1, 1L),
                        Position.of(product2, 2L));


        List<Position> basketContent = businessProcess.getBasketContent("");

        //noinspection unchecked
        assertThat(basketContent, containsInAnyOrder(
                Position.of(product1, 1L),
                Position.of(product2, 2L)));
    }

    @Test
    public void removeFromBasketUpdatesBasket() throws Exception {
        Position position = Position.of(productOf("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.removeFromBasket("", "5", 3L);

        verify(shoppingBasketRepository).save(shoppingBasketCaptor.capture());
        assertThat(shoppingBasketCaptor.getValue(), containsPosition(Position.of(productOf("5"), 1L)));
    }

    @Test(expected = DoesNotExistException.class)
    public void submitOrderWithEmptyBasketFails() throws Exception {
        stateSetup()
                .emptyShoppingBasket();

        businessProcess.submitOrder("3");
    }

    @Test
    public void submitOrderCreatesOrderWithPositions() throws Exception {
        Money price = Money.of(12d, EUR);
        Product product = productOf("5", price);
        stateSetup()
                .havingProducts(product)
                .havingShoppingBasket(Position.of(product, 4L));

        businessProcess.submitOrder("3");

        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue(), containsPricedPosition(PricedPosition.of(product, 4L, price)));

    }

    private Product productOf(String productId, Money price) {
        Product product = productOf(productId);
        product.setPrice(price);
        return product;
    }

    @Test
    public void submitOrderCreatesPickingOrder() throws Exception {
        Position position = Position.of(productOf("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(pickingOrderRepository).save(pickingOrderCaptor.capture());
        assertThat(pickingOrderCaptor.getValue(), not(containsPosition(position)));
        assertThat(pickingOrderCaptor.getValue().getStatus(), is(PickingOrder.PickingStatus.OPEN));
    }

    @Test
    public void submitOrderNotifiesStock() throws Exception {
        Position position = Position.of(productOf("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(stockNotification).newPickingOrder(pickingOrderCaptor.capture());
        assertThat(pickingOrderCaptor.getValue(), not(containsPosition(position)));
        assertThat(pickingOrderCaptor.getValue().getStatus(), is(PickingOrder.PickingStatus.OPEN));
    }

    @Test
    public void submitOrderClearsShoppingBasket() throws Exception {
        Position position = Position.of(productOf("5"), 4L);
        stateSetup()
                .havingShoppingBasket(position);

        businessProcess.submitOrder("3");

        verify(customerLookup).save(argThat(customer -> customer.getShoppingBasket().isEmpty()));
    }

    @Test
    public void submitOrderCreatesInvoice() throws Exception {
        Money price = Money.of(2d, EUR);
        Product product = productOf("5", price);
        stateSetup()
                .havingProducts(product)
                .havingShoppingBasket(Position.of(product, 4L));

        businessProcess.submitOrder("3");

        verify(invoiceRepository).save(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue(), containsPricedPosition(PricedPosition.of(product, 4L, price)));
    }

    @Test
    public void submitOrderDeliversInvoice() throws Exception {
        Money price = Money.of(2d, EUR);
        Product product = productOf("5", price);
        stateSetup()
                .havingProducts(product)
                .havingShoppingBasket(Position.of(product, 4L));

        businessProcess.submitOrder("3");

        verify(invoiceDelivery).deliver(invoiceCaptor.capture());
        assertThat(invoiceCaptor.getValue(), containsPricedPosition(PricedPosition.of(product, 4L, price)));
    }

    private StateSetup stateSetup() {
        return new StateSetup();
    }

    private Product productOf(String productId) {
        Product product = new Product();
        product.setId(productId);
        return product;
    }

    private class StateSetup {
        private Customer customer = new Customer();

        public StateSetup() {
            havingCustomer(customer);
        }

        private void havingCustomer(Customer customer) {
            doAnswer(invocation -> {
                Consumer<Customer> customerConsumer = invocation.getArgument(1);
                customerConsumer.accept(customer);
                return null;
            }).when(customerLookup).tryWithCustomer(anyString(), any());

            doAnswer(invocation -> {
                Function<Customer, ?> customerConsumer = invocation.getArgument(1);
                return customerConsumer.apply(customer);
            }).when(customerLookup).withCustomer(anyString(), any(), any());
        }

        @SuppressWarnings("unchecked")
        public StateSetup havingProducts(Product... products) {
            doAnswer(invocation -> {
                String id = invocation.getArgument(0);
                return stream(products)
                        .filter(p -> p.getId().equals(id))
                        .findFirst();
            }).when(productLookup).findOne(anyString());

            doAnswer(invocation -> {
                Function<Stream<Product>, ?> streamFunction = invocation.getArgument(1);
                return streamFunction.apply(
                        join(stream(products))
                        .withKey(Product::getId)
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
            return this;
        }

        public StateSetup emptyShoppingBasket() {
            Customer customer = getCustomer();
            customer.setShoppingBasket(new ShoppingBasket());
            return this;
        }

        public StateSetup havingStockOf(StockItem... stockItems) {
            doAnswer(invocation -> {
                String id = (String) invocation.getArguments()[0];
                //noinspection OptionalGetWithoutIsPresent
                return stream(stockItems)
                        .filter(stockItem -> stockItem.getProduct().getId().equals(id))
                        .findFirst();

            }).when(stockItemRepository).findByProductId(anyString());

            return this;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    private ShoppingBasket shoppingBasketWith(List<Position> positions) {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        positions.forEach(position -> shoppingBasket.put(position.getProduct(), position.getQuantity()));
        return shoppingBasket;
    }
}
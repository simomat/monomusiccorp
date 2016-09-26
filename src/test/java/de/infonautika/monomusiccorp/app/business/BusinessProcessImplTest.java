package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.BiDescribingMatcherBuilder;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.infonautika.monomusiccorp.app.DescribingMatcherBuilder.matcherForExpected;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShoppingBasketRepository shoppingBasketRepository;

    @Mock
    private SecurityService securityService;

    @Mock
    private CustomerLookup customerLookup;

    @Test
    public void addCustomerWithExistingNamefails() throws Exception {
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

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        assertThat(captor.getValue(), isRepresentedBy(customerOf(hans)));
    }

    private Matcher<Customer> isRepresentedBy(Customer customer) {
        return matcherForExpected(customer)
                .matchesWith((actual, expected) ->
                        areEqual(actual.getAddress(), expected.getAddress()) &&
                        areEqual(actual.getUsername(), expected.getUsername()))
                .describesTo((c) ->
                        c.getClass().getSimpleName() + " with address '" + c.getAddress().getAddressString() +
                                "' and username '" + c.getUsername() + "'")
                .andBuild();
    }

    private Customer customerOf(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setAddress(new Address(customerInfo.getAddress()));
        customer.setUsername(customerInfo.getUsername());
        return customer;
    }

    @Test
    public void addStockItemWithoutProductfails() throws Exception {
        stateSetup()
                .havingProducts();

        ResultStatus actual = businessProcess.addItemToStock(Quantity.of(new ItemId("10"), 5L));

        assertThat(actual, is(ResultStatus.NOT_EXISTENT));
    }

    @Test
    public void addStockItemCreatesNew() throws Exception {
        ItemId itemId = ItemId.of("10");
        Product product = productWith(itemId);

        when(productLookup.findOne(itemId.getId())).thenReturn(Optional.of(product));

        businessProcess.addItemToStock(Quantity.of(itemId, 5L));

        ArgumentCaptor<StockItem> captor = ArgumentCaptor.forClass(StockItem.class);
        verify(stockItemRepository).save(captor.capture());
        assertThat(captor.getValue(), equalsStockItem(StockItem.of(product, 5L)));
    }

    @Test
    public void addStockItemUpdatesExisting() throws Exception {
        stateSetup()
                .havingStockOf(StockItem.of(productWith(ItemId.of("10")), 5L));

        businessProcess.addItemToStock(Quantity.of(ItemId.of("10"), 6L));

        ArgumentCaptor<StockItem> captor = ArgumentCaptor.forClass(StockItem.class);
        verify(stockItemRepository).save(captor.capture());
        assertThat(captor.getValue(), equalsStockItem(StockItem.of(productWith(ItemId.of("10")), 11L)));
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
    public void putNonexistingItemToBasketFails() throws Exception {
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
        // Playing with future versions of mockito tells me, that argThat(Matcher<T> matcher) will be gone

//        verify(shoppingBasketRepository).save(argThat(containsPosition(Position.of(ItemId.of("5"), 3L))));

        ArgumentCaptor<ShoppingBasket> captor = ArgumentCaptor.forClass(ShoppingBasket.class);
        verify(shoppingBasketRepository).save(captor.capture());
        assertThat(captor.getValue(), containsPosition(Position.of(ItemId.of("4"), 3L)));
    }

    private Matcher<ShoppingBasket> containsPosition(Position position) {
        return BiDescribingMatcherBuilder
                .<Position, ShoppingBasket>matcherForExpected(position)
                    .withMatcher((actual, expected) -> actual.getPositions().contains(expected))
                    .withExpectedDescriber((expected) -> ShoppingBasket.class.getSimpleName() + " containing " + expected)
                    .withActualDescriber((actual) -> actual.getClass().getSimpleName() + " containsPosition [" +
                            actual.getPositions().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(", ")) + "]")
                    .andBuild();
    }

    @Test
    public void contentOfEmptyBasketIsEmpty() throws Exception {
        when(customerLookup.getShoppingBasketOfCustomer(anyString())).thenReturn(Optional.of(new ShoppingBasket()));

        List<Quantity<Product>> basketContent = businessProcess.getBasketContent("");

        assertThat(basketContent, is(empty()));
    }

    @Test
    public void contentOfBasketIsReturned() throws Exception {
        ItemId itemId1 = ItemId.of("1");
        ItemId itemId2 = ItemId.of("2");

        stateSetup()
                .havingProducts(
                        productWith(itemId1),
                        productWith(itemId2),
                        productWith(ItemId.of("3")))
                .havingShoppingBasket(
                        Position.of(itemId1, 1L),
                        Position.of(itemId2, 2L));


        List<Quantity<Product>> basketContent = businessProcess.getBasketContent("");

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

        ArgumentCaptor<ShoppingBasket> captor = ArgumentCaptor.forClass(ShoppingBasket.class);
        verify(shoppingBasketRepository).save(captor.capture());
        assertThat(captor.getValue(), containsPosition(Position.of(ItemId.of("5"), 1L)));
    }

    @Ignore
    @Test
    public void submitOrder() throws Exception {
        Customer customer = new Customer();
        customer.setId("3");
        stateSetup()
                .havingCustomers(customer);

        businessProcess.submitOrder("3");


        // T B C
    }

    private StateSetup stateSetup() {
        return new StateSetup();
    }


    private Product productWith(ItemId itemId) {
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
        @SuppressWarnings("unchecked")
        public StateSetup havingProducts(Product... products) {
            when(productLookup.findAll()).thenReturn(asList(products));

            doAnswer(invocation -> {
                String id = invocation.getArgumentAt(0, String.class);
                return stream(products)
                        .filter(p -> p.getItemId().getId().equals(id))
                        .findFirst();
            }).when(productLookup).findOne(anyString());

            doAnswer(invocation -> {
                Function<Stream<Product>, ?> streamFunction = (Function<Stream<Product>, ?>) invocation.getArgumentAt(1, Function.class);
                return streamFunction.apply(
                        join(stream(products))
                        .withKey(p -> p.getItemId().getId())
                        .on((Stream<String>) invocation.getArgumentAt(0, Stream.class))
                        .withKey(Function.identity())
                        .group((p, idStream) -> p)
                        .asStream());
            }).when(productLookup).withProducts(any(), any(), any());

            return this;
        }

        public StateSetup havingShoppingBasket(Position... positions) {
            when(customerLookup.getShoppingBasketOfCustomer(anyString()))
                    .thenReturn(Optional.ofNullable(shoppingBasketWith(asList(positions))));
            return this;
        }

        public StateSetup productExistsReturnsTrue() {
            when(productLookup.exists(anyString())).thenReturn(true);
            return this;
        }

        public StateSetup emptyShoppingBasket() {
            when(customerLookup.getShoppingBasketOfCustomer(anyString()))
                    .thenReturn(Optional.of(new ShoppingBasket()));
            return this;
        }

        public StateSetup havingStockOf(StockItem... stockItems) {
            doAnswer(invocation -> {
                String id = (String) invocation.getArguments()[0];
                return stream(stockItems)
                        .filter(stockItem -> stockItem.getProduct().getItemId().getId().equals(id))
                        .findFirst().get();

            }).when(stockItemRepository).findByProductId(anyString());

            return this;
        }

        public StateSetup havingCustomers(Customer... customers) {
            doAnswer(invocation -> {
                String id = (String) invocation.getArguments()[0];
                return stream(customers)
                        .filter(customer -> customer.getId().equals(id))
                        .findFirst();
            }).when(customerLookup).getCustomer(anyString());
            return this;
        }
    }

    private ShoppingBasket shoppingBasketWith(List<Position> positions) {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        positions.forEach(position -> shoppingBasket.put(position.getItemId(), position.getQuantity()));
        return shoppingBasket;
    }
}
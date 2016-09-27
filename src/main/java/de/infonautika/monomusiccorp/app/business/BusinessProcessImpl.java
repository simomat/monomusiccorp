package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static de.infonautika.monomusiccorp.app.business.ResultStatus.isOk;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    final Logger logger = LoggerFactory.getLogger(BusinessProcessImpl.class);

    @Autowired
    private StockItemRepository stockItemRepository;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PickingOrderRepository pickingOrderRepository;

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CustomerLookup customerLookup;

    @Autowired
    private ProductLookup productLookup;

    @Autowired
    private StockNotification stockNotification;

    @Override
    public Collection<Product> getAllProducts() {
        return productLookup.findAll();
    }

    @Override
    public ResultStatus addItemToStock(Quantity<ItemId> quantity) {
        return findStockItem(quantity.getItem())
                .map(stockItem -> {
                    updateStockItemQuantity(stockItem, quantity);
                    return ResultStatus.OK;
                })
                .orElseGet(() -> getProduct(quantity.getItem())
                        .map(product -> {
                            createStockItem(product, quantity);
                            return ResultStatus.OK;
                        }).
                        orElseGet(() -> {
                            logger.debug("no product of {} found to add stock item", quantity.getItem());
                            return ResultStatus.NOT_EXISTENT;
                        }));
    }

    private void createStockItem(Product product, Quantity<ItemId> quantity) {
        logger.debug("new stock item {} with quantity of {}", product.getItemId(), quantity.getQuantity());
        StockItem stockItem = StockItem.of(product, quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private void updateStockItemQuantity(StockItem stockItem, Quantity<ItemId> quantity) {
        assert Objects.equals(stockItem.getProduct().getItemId(), quantity.getItem());
        logger.debug("update stock item {} quantity with {}", quantity.getItem(), stockItem.getQuantity());
        stockItem.addQuantity(quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private Optional<StockItem> findStockItem(ItemId itemId) {
        return Optional.ofNullable(stockItemRepository.findByProductId(itemId.getId()));
    }

    private Optional<Product> getProduct(ItemId itemId) {
        return productLookup.findOne(itemId.getId());
   }

    @Override
    public Collection<StockItem> getStocks() {
        return stockItemRepository.findAll();
    }

    @Override
    public ResultStatus putToBasket(String customerId, Quantity<ItemId> quantity) {
        if (!itemExists(quantity.getItem())) {
            logger.debug("user id {} tried to put {} to basket, but product does not exist", customerId, quantity.getItem());
            return ResultStatus.NOT_EXISTENT;
        }

        return withCustomer(
                customerId,
                customer -> {
                    ShoppingBasket shoppingBasket = customer.getShoppingBasket();
                    shoppingBasket.put(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                    logger.debug("customer {} put {} to basket", customer.getUsername(), quantity);
                    return ResultStatus.OK;
                });
    }

    private boolean itemExists(ItemId item) {
        return productLookup.exists(item.getId());
    }

    @Override
    public List<Quantity<Product>> getBasketContent(String customerId) {
        return withCustomer(
                customerId,
                customer -> toProductQuantities(customer.getShoppingBasket().getPositions()),
                Collections::emptyList);
    }

    private List<Quantity<Product>> toProductQuantities(List<Position> positions) {
        return productLookup.withProducts(
                positions.stream()
                        .map(p -> p.getItemId().getId()),
                products ->
                    join(positions.stream())
                    .withKey(Position::getItemId)
                    .on(products)
                    .withKey(Product::getItemId)
                    .combine((pos, prod) ->
                            Quantity.of(prod, pos.getQuantity()))
                    .asStream()
                    .collect(toList()),
                Collections::emptyList);
    }

    @Override
    public void removeFromBasket(String customerId, Quantity<ItemId> quantity) {
        tryWithCustomer(customerId,
                customer -> {
                    ShoppingBasket shoppingBasket = customer.getShoppingBasket();
                    shoppingBasket.remove(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                    logger.debug("customer {} removed {} of {} from basket", customer.getUsername(), quantity.getQuantity(), quantity.getItem());
                });
    }

    @Override
    public ResultStatus addCustomer(CustomerInfo customer) {
        ResultStatus resultStatus = securityService.addUser(customer);
        if (!isOk(resultStatus)) {
            logger.info("failed to add customer {}", customer.getUsername());
            return resultStatus;
        }

        createCustomer(customer);
        logger.info("customer {} added", customer.getUsername());
        return ResultStatus.OK;
    }

    @Override
    public ResultStatus submitOrder(String customerId) {
        return withCustomer(
                customerId,
                customer -> {
                    ShoppingBasket shoppingBasket = customer.getShoppingBasket();
                    if (shoppingBasket.isEmpty()) {
                        logger.debug("rejected submit an order with empty basket for customer {}", customer.getUsername());
                        return ResultStatus.NOT_EXISTENT;
                    }

                    Order order = createOrder(customer, shoppingBasket);
                    PickingOrder pickingOrder = createPickingOrder(order);
                    notifyNewPickingOrder(pickingOrder);

                    clearShoppingBasket(customer);

                    sendInvoice(order);
                    return ResultStatus.OK;
                });
    }

    private void clearShoppingBasket(Customer customer) {
        customer.getShoppingBasket().clear();
        customerLookup.save(customer);
    }

    private ResultStatus withCustomer(String customerId, Function<Customer, ResultStatus> customerMapper) {
        return withCustomer(customerId, customerMapper, () -> ResultStatus.NO_CUSTOMER);
    }

    private <T> T withCustomer(String customerId, Function<Customer, T> customerMapper, Supplier<T> elseGet) {
        return customerLookup.getCustomer(customerId)
                .map(customerMapper)
                .orElseGet(() -> {
                    logger.debug("no customer with id {} found", customerId);
                    return elseGet.get();
                });
    }

    private void tryWithCustomer(String customerId, Consumer<Customer> consumer) {
        customerLookup.getCustomer(customerId)
                .ifPresent(consumer);
    }

    @Override
    public List<Order> getOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    private void sendInvoice(Order order) {

    }

    private PickingOrder createPickingOrder(Order order) {
        PickingOrder pickingOrder = new PickingOrder();
        pickingOrder.setPickedItems(emptyList());
        pickingOrder.setStatus(PickingOrder.PickingStatus.OPEN);
        order.setPickingOrder(pickingOrder);
        pickingOrderRepository.save(pickingOrder);
        logger.debug("picking order {} for order {} created", pickingOrder.getId(), order.getId());
        return pickingOrder;
    }

    private void notifyNewPickingOrder(PickingOrder pickingOrder) {
        stockNotification.newPickingOrder(pickingOrder);
    }

    private Order createOrder(Customer customer, ShoppingBasket shoppingBasket) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setPositions(shoppingBasket.getPositions());
        order.setShippingAddress(customer.getAddress());
        order.setSubmitTime(LocalDateTime.now());
        orderRepository.save(order);
        logger.debug("order {} for customer {} created", order.getId(), customer.getUsername());
        return order;
    }

    private void createCustomer(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setUsername(customerInfo.getUsername());
        customer.setAddress(new Address(customerInfo.getAddress()));
        customerLookup.save(customer);
    }

}

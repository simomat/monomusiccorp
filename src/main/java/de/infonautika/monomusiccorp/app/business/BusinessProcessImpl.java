package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDelivery invoiceDelivery;

    @Override
    public ResultStatus addItemToStock(Quantity<String> quantity) {
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

    private void createStockItem(Product product, Quantity<String> quantity) {
        logger.debug("new stock item {} with quantity of {}", product.getId(), quantity.getQuantity());
        StockItem stockItem = StockItem.of(product, quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private void updateStockItemQuantity(StockItem stockItem, Quantity<String> quantity) {
        assert Objects.equals(stockItem.getProduct().getId(), quantity.getItem());
        logger.debug("update stock item {} quantity with {}", quantity.getItem(), stockItem.getQuantity());
        stockItem.addQuantity(quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private Optional<StockItem> findStockItem(String productId) {
        return Optional.ofNullable(stockItemRepository.findByProductId(productId));
    }

    private Optional<Product> getProduct(String productId) {
        return productLookup.findOne(productId);
   }

    @Override
    public ResultStatus putToBasket(String customerId, Quantity<String> quantity) {
        return productLookup.findOne(quantity.getItem())
                .map(product ->
                    withCustomer(
                        customerId,
                        customer -> {
                            ShoppingBasket shoppingBasket = customer.getShoppingBasket();
                            shoppingBasket.put(product, quantity.getQuantity());
                            shoppingBasketRepository.save(shoppingBasket);
                            logger.debug("customer {} put {} to basket", customer.getUsername(), quantity);
                            return ResultStatus.OK;
                        }))
                .orElseGet(() -> {
                    logger.debug("user id {} tried to put {} to basket, but product does not exist", customerId, quantity.getItem());
                    return ResultStatus.NOT_EXISTENT;
                });
    }

    @Override
    public List<Position> getBasketContent(String customerId) {
        return customerLookup.withCustomer(
                customerId,
                customer -> customer.getShoppingBasket().getPositions(),
                Collections::emptyList);
    }

    @Override
    public void removeFromBasket(String customerId, Quantity<String> quantity) {
        customerLookup.tryWithCustomer(customerId,
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

                    Order order = createAndSaveOrder(customer, shoppingBasket);
                    clearShoppingBasket(customer);
                    processNewOrder(order);

                    return ResultStatus.OK;
                });
    }

    private Order createAndSaveOrder(Customer customer, ShoppingBasket shoppingBasket) {
        Order order = createOrder(customer);
        orderRepository.save(order);
        logger.debug("created order for customer {} with {}", customer.getUsername(), shoppingBasket.getPositions());
        return order;
    }

    private void processNewOrder(Order order) {
        processShipment(order);
        processBilling(order);
    }

    private void processShipment(Order order) {
        PickingOrder pickingOrder = createPickingOrder(order);
        notifyNewPickingOrder(pickingOrder);
    }

    private void clearShoppingBasket(Customer customer) {
        customer.getShoppingBasket().clear();
        customerLookup.save(customer);
    }

    private ResultStatus withCustomer(String customerId, Function<Customer, ResultStatus> customerMapper) {
        return customerLookup.withCustomer(customerId, customerMapper, () -> ResultStatus.NO_CUSTOMER);
    }

    @Override
    public List<PickingOrder> getPickingOrders(String customerId) {
        return pickingOrderRepository.findByOrderCustomerId(customerId);
    }

    private void processBilling(Order order) {
        Invoice invoice = createInvoice(order);
        invoiceDelivery.deliver(invoice);
    }

    private Invoice createInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setDate(LocalDate.now());
        invoiceRepository.save(invoice);
        return invoice;
    }

    private PickingOrder createPickingOrder(Order order) {
        PickingOrder pickingOrder = new PickingOrder();
        pickingOrder.setPickedItems(emptyList());
        pickingOrder.setStatus(PickingOrder.PickingStatus.OPEN);
        pickingOrder.setOrder(order);
        pickingOrderRepository.save(pickingOrder);
        logger.debug("created picking order for customer {} with {}", order.getCustomer().getUsername(), order.getPositions());
        return pickingOrder;
    }

    private void notifyNewPickingOrder(PickingOrder pickingOrder) {
        stockNotification.newPickingOrder(pickingOrder);
    }

    private Order createOrder(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setPositions(toPricedPositions(customer.getShoppingBasket().getPositions()));
        order.setShippingAddress(customer.getAddress());
        order.setSubmitTime(LocalDateTime.now());
        return order;
    }

    private List<PricedPosition> toPricedPositions(List<Position> positions) {
        return productLookup.withProducts(
                positions.stream().map(p -> p.getProduct().getId()),
                products ->
                    join(products)
                        .withKey(Product::getId)
                        .on(positions.stream())
                        .withKey(position -> position.getProduct().getId())
                        .combine(this::toPricedPosition)
                        .asStream()
                        .collect(toList()),
                Collections::emptyList);
    }

    private PricedPosition toPricedPosition(Product prod, Position pos) {
        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setProduct(pos.getProduct());
        pricedPosition.setQuantity(pos.getQuantity());
        pricedPosition.setPrice(prod.getPrice());
        return pricedPosition;
    }

    private void createCustomer(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setUsername(customerInfo.getUsername());
        customer.setAddress(new Address(customerInfo.getAddress()));
        customerLookup.save(customer);
    }

}

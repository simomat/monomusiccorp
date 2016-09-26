package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.infonautika.monomusiccorp.app.business.ResultStatus.isOk;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

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

    @Override
    public Collection<Product> getAllProducts() {
        return productRepo.findAll();
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
                        orElse(ResultStatus.NOT_EXISTENT));
    }

    private void createStockItem(Product product, Quantity<ItemId> quantity) {
        StockItem stockItem = StockItem.of(product, quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private void updateStockItemQuantity(StockItem stockItem, Quantity<ItemId> quantity) {
        assert Objects.equals(stockItem.getProduct().getItemId(), quantity.getItem());
        stockItem.addQuantity(quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private Optional<StockItem> findStockItem(ItemId itemId) {
        return Optional.ofNullable(stockItemRepository.findByProductId(itemId.getId()));
    }

    private Optional<Product> getProduct(ItemId itemId) {
        return Optional.ofNullable(productRepo.findOne(itemId.getId()));
   }

    @Override
    public Collection<StockItem> getStocks() {
        return stockItemRepository.findAll();
    }

    @Override
    public ResultStatus putToBasket(String customerId, Quantity<ItemId> quantity) {
        if (!itemExists(quantity.getItem())) {
            return ResultStatus.NOT_EXISTENT;
        }

        customerLookup.getShoppingBasketOfCustomer(customerId)
                .ifPresent(shoppingBasket -> {
                    shoppingBasket.put(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                });

        return ResultStatus.OK;
    }

    private boolean itemExists(ItemId item) {
        return productRepo.exists(item.getId());
    }

    @Override
    public List<Quantity<Product>> getBasketContent(String customerId) {
        return customerLookup.getShoppingBasketOfCustomer(customerId)
                .map(ShoppingBasket::getPositions)
                .map(this::toProductQuantities)
                .orElse(emptyList());
    }

    private List<Quantity<Product>> toProductQuantities(List<Position> positions) {
        try (Stream<Product> products = findProductsById(positions)) {
            return join(positions.stream())
                    .withKey(Position::getItemId)
                    .on(products)
                    .withKey(Product::getItemId)
                    .combine((pos, prod) ->
                            Quantity.of(prod, pos.getQuantity()))
                    .asStream()
                    .collect(toList());
        }
    }

    private Stream<Product> findProductsById(List<Position> positions) {
        List<String> ids = positions.stream()
                .map(p -> p.getItemId().getId())
                .collect(toList());
        Stream<Product> productStream = productRepo.findByIdIn(
                ids);
        if (productStream == null) {
            return Stream.empty();
        }
        return productStream;
    }

    @Override
    public void removeFromBasket(String customerId, Quantity<ItemId> quantity) {
        customerLookup.getShoppingBasketOfCustomer(customerId)
                .ifPresent(shoppingBasket -> {
                    shoppingBasket.remove(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                });
    }

    @Override
    public ResultStatus addCustomer(CustomerInfo customer) {
        ResultStatus resultStatus = securityService.addUser(customer);
        if (!isOk(resultStatus)) {
            return resultStatus;
        }

        createCustomer(customer);
        return ResultStatus.OK;
    }

    @Override
    public ResultStatus submitOrder(String customerId) {
        return withCustomer(
                customerId,
                customer -> {
                    ShoppingBasket shoppingBasket = customer.getShoppingBasket();
                    if (shoppingBasket.isEmpty()) {
                        return ResultStatus.NOT_EXISTENT;
                    }

                    Order order = createOrder(customer, shoppingBasket);
                    newPickingOrder(order);
                    sendInvoice(order);
                    return ResultStatus.OK;
                });
    }

    private ResultStatus withCustomer(String customerId, Function<Customer, ResultStatus> consumer) {
        return customerLookup.getCustomer(customerId)
                .map(consumer)
                .orElse(ResultStatus.NO_CUSTOMER);
    }

    @Override
    public List<Order> getOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    private void sendInvoice(Order order) {

    }

    private void newPickingOrder(Order order) {
        PickingOrder pickingOrder = new PickingOrder();
        pickingOrder.setPickedItems(emptyList());
        pickingOrder.setStatus(PickingOrder.PickingStatus.OPEN);
        order.setPickingOrder(pickingOrder);
        pickingOrderRepository.save(pickingOrder);
        notifyNewPickingOrder();
    }

    private void notifyNewPickingOrder() {

    }

    private Order createOrder(Customer customer, ShoppingBasket shoppingBasket) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setPositions(shoppingBasket.getPositions());
        order.setShippingAddress(customer.getAddress());
        order.setSubmitTime(LocalDateTime.now());
        orderRepository.save(order);
        return order;
    }

    private void createCustomer(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setUsername(customerInfo.getUsername());
        customer.setAddress(new Address(customerInfo.getAddress()));
        customerRepository.save(customer);
    }

}
